package com.sag.pagent.broker;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.broker.behaviours.BuyProducts;
import com.sag.pagent.broker.behaviours.QueryShopsBehaviour;
import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.broker.messages.RegisterShopAgent;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.shop.messages.ArticlesStatusReply;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static com.sag.pagent.Constant.QUERY_ARTICLES_TIME;

@Slf4j
public class BrokerAgent extends BasicAgent implements QueryShopsBehaviour.QueryShopsBehaviourListener {
    private ReceiveMessagesBehaviour receiveMessages;
    private QueryShopsBehaviour queryShopsBehaviour;
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
        } else if (content instanceof BuyProductsRequest) {
            logReceivedMessage(msg, BuyProductsRequest.class);
            handleBuyProductsRequest(msg);
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    private void handleRegisterShopAgent(ACLMessage msg) {
        try {
            log.info("Register ShopAgent: {}", msg.getSender().getLocalName());
            RegisterShopAgent registerShopAgent = (RegisterShopAgent) msg.getContentObject();
            queryShopsBehaviour.addShopAgent(new AID(registerShopAgent.getShopAgentName(), AID.ISGUID));
        } catch (UnreadableException e) {
            log.error("Exception while handling RegisterShopAgent message", e);
        }
    }

    private void handleBuyProductsRequest(ACLMessage msg) {
        try {
            BuyProductsRequest buyProductsRequest = (BuyProductsRequest) msg.getContentObject();
            List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(buyProductsRequest);
            if (shopArticleList.isEmpty()) {
                replyNotBuy(msg, buyProductsRequest);
                return;
            }

            BuyProducts buyProducts = new BuyProducts(this, msg, buyProductsRequest, shopArticleList);
            buyProducts.getMessages().forEach(this::send);
            buyProducts.getHandleOneRespondList().forEach(receiveMessages::registerRespond);
        } catch (IOException e) {
            log.error("Exception while serializing BuyProductsResponse", e);
        } catch (UnreadableException e) {
            log.error("Exception while handling PurchaseOrder message", e);
        }
    }

    private void replyNotBuy(ACLMessage msg, BuyProductsRequest buyProductsRequest) throws IOException {
        ACLMessage reply = msg.createReply();
        reply.setContentObject(new BuyProductsResponse(0, 0d, buyProductsRequest));
        send(reply);
    }

    @Override
    public void onArticlesStatusReply(ACLMessage msg, ArticlesStatusReply articlesStatusReply) {
        log.debug("onArticlesStatusReply");
        articleOrganizer.setArticleList(msg.getSender(), articlesStatusReply.getArticlesToSell());
    }
}
