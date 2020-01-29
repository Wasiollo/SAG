package com.sag.pagent.manager;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.HandleOneRespond;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.customer.order.OrderArticle;
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

import java.util.*;

import static com.sag.pagent.Constant.BUY_PRODUCTS_TIME_TO_RESPOND;
import static com.sag.pagent.Constant.SAMPLING_ALGORITHM;

@Slf4j
public class ManagerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
    private Double budget = 0d;
    private Map<ArticleType, Integer> articlesToBuy = new EnumMap<>(ArticleType.class);
    private BrokerHierarchy brokerHierarchy;

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
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        addBehaviour(receiveMessages);
        if (SAMPLING_ALGORITHM) {
            brokerHierarchy = new BrokerSamplingHierarchy();
        } else {
            brokerHierarchy = new BrokerBudgetQuantizationHierarchy();
        }
        brokerHierarchy.initializeHierarchy(ServiceUtils.findAgentList(this, ServiceType.BROKER));
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
        purchaseOrders.add(purchaseOrder);
        budget += purchaseOrder.getBudget();
        addArticlesToBuy(purchaseOrder.getArticlesToBuy());
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        send(reply);
        log.info("Handled Purchase order from conversation: " + msg.getConversationId());
        log.debug("Budget: " + budget);
    }

    private void addArticlesToBuy(List<OrderArticle> articles) {
        articles.forEach(article -> {
            if (articlesToBuy.containsKey(article.getArticle())) {
                Integer articleAmount = articlesToBuy.get(article.getArticle()) + article.getAmount();
                articlesToBuy.put(article.getArticle(), articleAmount);
                log.debug("Article {} existed, amount set to : {}", article.getArticle(), articleAmount);
            } else {
                articlesToBuy.put(article.getArticle(), article.getAmount());
                log.debug("Article {} didn't exist, amount set to: {}", article.getArticle(), article.getAmount());
            }
        });
    }

    public class BuyProductsRespond extends HandleOneRespond {
        public BuyProductsRespond(Agent agent, ACLMessage sendMessage) {
            super(agent, sendMessage);
        }

        @Override
        protected void action(ACLMessage msg) throws UnreadableException {
            log.debug("BuyProductRespond from: {}", msg.getSender().getLocalName());
            Object content = msg.getContentObject();
            if (content instanceof BuyProductsResponse) {
                BuyProductsResponse response = (BuyProductsResponse) content;
                updateHierarchy(msg.getSender(), response);
                sendBuyProductsToBrokerAgents(msg.getSender(), response.getRequest().getArticle());
            }
            finished();
        }

        @Override
        protected void onTimeout() {
            super.onTimeout();
//            TODO :)
        }
    }

    private void updateHierarchy(AID sender, BuyProductsResponse response) {
        brokerHierarchy.updateHierarchy(sender, response);
    }

    private void sendBuyProductsToBrokerAgents(AID broker, ArticleType articleType) {
        ACLMessage message = BuyProductsDispatcher.prepareBuyProductsMessage(
                broker,
                prepareProductToBuyByBroker(broker, articleType)
        );
        Date respondDate = new Date();
        respondDate.setTime(respondDate.getTime() + BUY_PRODUCTS_TIME_TO_RESPOND);
        message.setReplyByDate(respondDate);
        send(message);
        receiveMessages.registerRespond(new BuyProductsRespond(this, message));

    }

    private BuyProductsRequest prepareProductToBuyByBroker(AID broker, ArticleType articleType) {
//        TODO :)
        return null;
    }
}
