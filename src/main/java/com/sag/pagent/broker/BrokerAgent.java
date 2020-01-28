package com.sag.pagent.broker;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.broker.behaviours.QueryShopsBehaviour;
import com.sag.pagent.broker.messages.RegisterShopAgent;
import com.sag.pagent.manager.messages.PurchaseOrder;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.shop.messages.ArticlesStatusReply;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.sag.pagent.Constant.QUERY_ARTICLES_TIME;

@Slf4j
public class BrokerAgent extends BasicAgent implements QueryShopsBehaviour.QueryShopsBehaviourListener {
    private ReceiveMessagesBehaviour receiveMessages;
    private QueryShopsBehaviour queryShopsBehaviour;
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
    private ArticleOrganizer articleOrganizer = new ArticleOrganizer();

    public BrokerAgent() {
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        queryShopsBehaviour = new QueryShopsBehaviour(this, QUERY_ARTICLES_TIME, receiveMessages, this);
    }

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceBroker());
    }

    private ServiceDescription getServiceBroker() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.BROKER.getType();
        sd.setType(type);
        sd.setName(type);
        return sd;
    }

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(receiveMessages);
        addBehaviour(queryShopsBehaviour);
    }

    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        Object content = msg.getContentObject();

        if (content instanceof RegisterShopAgent) {
            logReceivedMessage(msg, RegisterShopAgent.class);
            handleRegisterShopAgent(msg);
        } else if (content instanceof PurchaseOrder) {
            logReceivedMessage(msg, PurchaseOrder.class);
            handlePurchaseOrder(msg);
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    private void handleRegisterShopAgent(ACLMessage msg) {
        try {
            RegisterShopAgent registerShopAgent = (RegisterShopAgent) msg.getContentObject();
            queryShopsBehaviour.addShopAgent(new AID(registerShopAgent.getShopAgentName(), AID.ISGUID));
        } catch (UnreadableException e) {
            log.error("Exception while handling RegisterShopAgent message", e);
        }
    }

    private void handlePurchaseOrder(ACLMessage msg) {
        try {
            PurchaseOrder purchaseOrder = (PurchaseOrder) msg.getContentObject();
            purchaseOrders.add(purchaseOrder);

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            send(reply);
        } catch (UnreadableException e) {
            log.error("Exception while handling PurchaseOrder message", e);
        }
    }

    @Override
    public void onArticlesStatusReply(ACLMessage msg, ArticlesStatusReply articlesStatusReply) {
        log.debug("onArticlesStatusReply");
        articleOrganizer.setArticleList(msg.getSender(), articlesStatusReply.getArticlesToSell());
    }
}
