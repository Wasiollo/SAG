package com.sag.pagent.shop;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.broker.messages.RegisterShopAgent;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.shop.articles.ArticleStorage;
import com.sag.pagent.shop.behaviors.FindAgentBehaviour;
import com.sag.pagent.shop.behaviors.RegenerateShopSuppliesBehaviour;
import com.sag.pagent.shop.messages.ArticlesStatusQuery;
import com.sag.pagent.shop.messages.ArticlesStatusReply;
import com.sag.pagent.shop.messages.PurchaseArticle;
import com.sag.pagent.shop.messages.PurchaseReport;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.sag.pagent.Constant.MAX_BROKERS_REGISTRATION_PER_SHOP;
import static com.sag.pagent.Constant.REGENERATE_SUPPLIES_TIME;

@Slf4j
public class ShopAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private FindAgentBehaviour findAgentBehaviour;
    private RegenerateShopSuppliesBehaviour regenerateShopSuppliesBehaviour;
    private List<AID> brokerAgent;
    private ArticleStorage articleStorage;

    public ShopAgent() {
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        findAgentBehaviour = new FindAgentBehaviour(this, 1000, agentFoundListener, ServiceType.BROKER,
                MAX_BROKERS_REGISTRATION_PER_SHOP);
        regenerateShopSuppliesBehaviour = new RegenerateShopSuppliesBehaviour(this, REGENERATE_SUPPLIES_TIME, supplyGeneratedListener);
        articleStorage = new ArticleStorage();
    }

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceShop());
    }

    private ServiceDescription getServiceShop() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.SHOP.getType();
        sd.setType(type);
        sd.setName(type);
        return sd;
    }

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(receiveMessages);
        addBehaviour(findAgentBehaviour);
        addBehaviour(regenerateShopSuppliesBehaviour);
    }

    private FindAgentBehaviour.AgentFoundListener agentFoundListener = agentList -> {
        brokerAgent = agentList;
        registerInBrokerAgents();
    };

    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        Object content = msg.getContentObject();

        if (content instanceof ArticlesStatusQuery) {
            logReceivedMessage(msg, ArticlesStatusQuery.class);
            handleArticlesStatusQuery(msg);
        } else if (content instanceof PurchaseArticle) {
            logReceivedMessage(msg, PurchaseArticle.class);
            handlePurchaseArticle(msg);
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    private RegenerateShopSuppliesBehaviour.SupplyGeneratedListener supplyGeneratedListener = supplies -> articleStorage.extend(supplies);

    private void registerInBrokerAgents() {
        log.debug("register itself in {} agents: {}", ServiceType.BROKER, getBrokerAgentNameList());
        ACLMessage msg = MessagesUtils.createMessage(ACLMessage.INFORM);
        brokerAgent.forEach(msg::addReceiver);
        try {
            msg.setContentObject(new RegisterShopAgent(getName()));
            send(msg);
        } catch (IOException ex) {
            log.error("IOException in registerInBrokerAgent", ex);
        }
    }

    private List<String> getBrokerAgentNameList() {
        return brokerAgent.stream().map(AID::getLocalName).collect(Collectors.toList());
    }

    private void handleArticlesStatusQuery(ACLMessage msg) throws UnreadableException {
        try {
            ACLMessage reply = msg.createReply();
            ArticlesStatusQuery articlesStatusQuery = (ArticlesStatusQuery) msg.getContentObject();
            ArticlesStatusReply articlesStatusReply = createArticlesStatusReply(articlesStatusQuery);
            reply.setContentObject(articlesStatusReply);
            send(reply);
        } catch (IOException e) {
            log.error("Exception while handling ArticlesStatusQuery message", e);
        }
    }

    @SuppressWarnings("unused")
    private ArticlesStatusReply createArticlesStatusReply(ArticlesStatusQuery articlesStatusQuery) {
        log.trace("createArticlesStatusReply");
        return new ArticlesStatusReply(articleStorage.getArticles());
    }

    private void handlePurchaseArticle(ACLMessage msg) throws UnreadableException {
        try {
            ACLMessage reply = msg.createReply();
            PurchaseArticle purchaseArticle = (PurchaseArticle) msg.getContentObject();
            PurchaseReport purchaseReport = articleStorage.purchase(purchaseArticle);
            reply.setContentObject(purchaseReport);
            send(reply);
        } catch (IOException e) {
            log.error("Exception while handling PurchaseArticle message", e);
        }
    }
}
