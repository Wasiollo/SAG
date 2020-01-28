package com.sag.pagent.manager;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.HandleRespond;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.customer.order.OrderArticle;
import com.sag.pagent.manager.messages.PurchaseOrder;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ManagerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
    private Double budget = 0d;
    private Map<ArticleType, Integer> articlesToBuy = new EnumMap<>(ArticleType.class);

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
                log.debug("Article existed, amount set to :" + articleAmount);
            } else {
                articlesToBuy.put(article.getArticle(), article.getAmount());
                log.debug("Article didn't exist, amount set to: " + article.getAmount());
            }
        });
    }

    public class BuyProductsRespond extends HandleRespond {
        public BuyProductsRespond(Agent agent, ACLMessage sendMessage) {
            super(agent, sendMessage);
        }

        @Override
        protected void action(ACLMessage msg) throws UnreadableException {
            log.debug("BuyProductRespond from: {}", msg.getSender().getLocalName());
//           TODO :)
            finished();
        }

        @Override
        protected void onTimeout() {
            super.onTimeout();
//            TODO :)
        }
    }
}
