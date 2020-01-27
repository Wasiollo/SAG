package com.sag.pagent.shop;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.broker.messages.RegisterShopAgent;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.behaviors.FindAgentBehaviour;
import com.sag.pagent.shop.behaviors.RegenerateShopSuppliesBehaviour;
import com.sag.pagent.shop.messages.ArticlesStatusQuery;
import com.sag.pagent.shop.messages.ArticlesStatusReply;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sag.pagent.Constant.MAX_BROKERS_REGISTRATION_PER_SHOP;
import static com.sag.pagent.Constant.REGENERATE_SUPPLIES_TIME;

@Slf4j
public class ShopAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private FindAgentBehaviour findAgentBehaviour;
    private RegenerateShopSuppliesBehaviour regenerateShopSuppliesBehaviour;
    private List<AID> brokerAgent;
    private List<Article> shopArticles = new ArrayList<>();

    public ShopAgent() {
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        findAgentBehaviour = new FindAgentBehaviour(this, 1000, agentFoundListener, ServiceType.BROKER,
                MAX_BROKERS_REGISTRATION_PER_SHOP);
        regenerateShopSuppliesBehaviour = new RegenerateShopSuppliesBehaviour(this, REGENERATE_SUPPLIES_TIME, supplyGeneratedListener);
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
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    private RegenerateShopSuppliesBehaviour.SupplyGeneratedListener supplyGeneratedListener = supplies -> {
        for (Article article : supplies) {
            Optional<Article> shopArticle = shopArticles.stream()
                    .filter(art -> art.getName().equals(article.getName()))
                    .findAny();
            if (shopArticle.isPresent()) {
                shopArticle.get().addAmount(article.getAmount());
                shopArticle.get().setPrice(article.getPrice());
            } else {
                shopArticles.add(article);
            }
        }
    };

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

    private void handleArticlesStatusQuery(ACLMessage msg) {
        try {
            ACLMessage reply = msg.createReply();
            ArticlesStatusQuery articlesStatusQuery = (ArticlesStatusQuery) msg.getContentObject();
            ArticlesStatusReply articlesStatusReply = createArticlesStatusReply(articlesStatusQuery);
            reply.setContentObject(articlesStatusReply);
            send(reply);
        } catch (UnreadableException | IOException e) {
            log.error("Exception while handling ArticlesStatusQuery message", e);
        }
    }

    @SuppressWarnings("unused")
    private ArticlesStatusReply createArticlesStatusReply(ArticlesStatusQuery articlesStatusQuery) {
        log.trace("createArticlesStatusReply");
        return new ArticlesStatusReply(shopArticles);
    }

    @SuppressWarnings("unused")
    private List<Article> filterArticleList(List<Article> articlesToBuy) {
        List<Article> articlesToStatusReply = new ArrayList<>();
        articlesToBuy.forEach(atb ->
                shopArticles.stream()
                        .filter(sa -> atb.getName().equals(sa.getName()))
                        .findAny()
                        .ifPresent(sa -> {
                            if (sa.getAmount() < atb.getAmount()) {
                                articlesToStatusReply.add(sa);
                            } else {
                                Article articleOffer = Article.builder()
                                        .name(sa.getName())
                                        .price(sa.getPrice())
                                        .amount(atb.getAmount())
                                        .build();
                                articlesToStatusReply.add(articleOffer);
                            }
                        }));
        return articlesToStatusReply;
    }
}
