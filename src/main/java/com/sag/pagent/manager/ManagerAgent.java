package com.sag.pagent.manager;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.HandleOneRespond;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.manager.hierarchy.BrokerBudgetQuantizationHierarchy;
import com.sag.pagent.manager.hierarchy.BrokerHierarchy;
import com.sag.pagent.manager.hierarchy.BrokerSamplingHierarchy;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import com.sag.pagent.manager.messages.PurchaseOrder;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import static com.sag.pagent.Constant.BUY_PRODUCTS_TIME_TO_RESPOND;
import static com.sag.pagent.Constant.SAMPLING_ALGORITHM;

@Slf4j
public class ManagerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private PurchaseOrderManager purchaseOrderManager = new PurchaseOrderManager();
    private BrokerHierarchy brokerHierarchy;

    public ManagerAgent() {
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        if (SAMPLING_ALGORITHM) {
            brokerHierarchy = new BrokerSamplingHierarchy(purchaseOrderManager);
        } else {
            brokerHierarchy = new BrokerBudgetQuantizationHierarchy(purchaseOrderManager);
        }
    }

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceManager());
    }

    private ServiceDescription getServiceManager() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.MANAGER.getType();
        sd.setType(type);
        sd.setName(type);
        return sd;
    }

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(receiveMessages);
    }

    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        Object content = msg.getContentObject();

        if (content instanceof PurchaseOrder) {
            logReceivedMessage(msg, PurchaseOrder.class);
            handlePurchaseOrder(msg);
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    private void handlePurchaseOrder(ACLMessage msg) throws UnreadableException {
        PurchaseOrder purchaseOrder = (PurchaseOrder) msg.getContentObject();
        log.info("Handling purchaseOrder from " + purchaseOrder.getCustomerAgentId());
        purchaseOrderManager.addPurchaseOrder(purchaseOrder);
        brokerHierarchy.updateBrokers(ServiceUtils.findAgentList(this, ServiceType.BROKER));
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        send(reply);
        sendInitialBuyMessages(purchaseOrder.getCustomerAgentId());
    }

    private void sendInitialBuyMessages(String customerAgentId) {
        purchaseOrderManager.getArticleTypeList(customerAgentId).forEach(articleType ->
                brokerHierarchy.getAliveBrokerList().forEach(broker ->
                        sendBuyProductsToBrokerAgents(customerAgentId, broker, articleType)));
    }

    private void sendBuyProductsToBrokerAgents(String customerAgentId, AID broker, ArticleType articleType) {
        log.debug("sendBuyProductsToBrokerAgents articleType: {} to: {}", articleType, broker.getLocalName());
        BuyProductsRequest buyProductsRequest = prepareProductToBuyByBroker(customerAgentId, broker, articleType);
        if (buyProductsRequest == null) return;
        ACLMessage message = BuyProductsDispatcher.prepareBuyProductsMessage(broker, buyProductsRequest);
        Date respondDate = new Date();
        respondDate.setTime(respondDate.getTime() + BUY_PRODUCTS_TIME_TO_RESPOND);
        message.setReplyByDate(respondDate);
        send(message);
        receiveMessages.registerRespond(new BuyProductsRespond(this, message, customerAgentId, buyProductsRequest));
    }

    private BuyProductsRequest prepareProductToBuyByBroker(String customerAgentId, AID broker, ArticleType articleType) {
        int amount = (int) (10 * brokerHierarchy.getMultiplier(articleType, broker));
        double budget = 100d * brokerHierarchy.getMultiplier(articleType, broker);

        amount = purchaseOrderManager.getMinAmount(customerAgentId, articleType, amount);
        budget = purchaseOrderManager.getMinBudget(customerAgentId, budget);

        if (amount == 0 || budget == 0) {
            log.info("Nothing to buy for ArticleType: {}, amount: {}, budget: {}", articleType, amount, budget);
            return null;
        }

        return new BuyProductsRequest(articleType, amount, budget);
    }

    public class BuyProductsRespond extends HandleOneRespond {
        private final String customerAgentId;
        private final BuyProductsRequest buyProductsRequest;

        public BuyProductsRespond(Agent agent, ACLMessage sendMessage, String customerAgentId, BuyProductsRequest buyProductsRequest) {
            super(agent, sendMessage);
            this.customerAgentId = customerAgentId;
            this.buyProductsRequest = buyProductsRequest;
        }

        @Override
        protected void action(ACLMessage msg) throws UnreadableException {
            log.debug("BuyProductRespond from: {}", msg.getSender().getLocalName());
            Object content = msg.getContentObject();
            if (content instanceof BuyProductsResponse) {
                BuyProductsResponse response = (BuyProductsResponse) content;
                brokerHierarchy.updateHierarchy(msg.getSender(), response, buyProductsRequest);
                purchaseOrderManager.recover(customerAgentId, response);
                if (brokerHierarchy.isFinished(response.getRequest().getArticleType())) {
                    log.info("Buying ArticleType {} is finished", response.getRequest().getArticleType());
                } else {
                    sendBuyProductsToBrokerAgents(customerAgentId, msg.getSender(), response.getRequest().getArticleType());
                }
            }
            finished();
        }

        @Override
        protected void onTimeout() {
            super.onTimeout();
            log.debug("Lost {} budget", buyProductsRequest.getBudget());
            purchaseOrderManager.addAmount(customerAgentId, buyProductsRequest.getArticleType(), buyProductsRequest.getAmount());
        }
    }
}
