package com.sag.pagent.agents;

import com.sag.pagent.behaviors.FindAgentBehaviour;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.behaviors.RegenerateShopSuppliesBehaviour;
import com.sag.pagent.messages.ArticlesStatusQuery;
import com.sag.pagent.messages.ArticlesStatusReply;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.messages.RegisterShopAgent;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.shop.domain.Article;
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

@Slf4j
public class ShopAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private AID brokerAgent;
    private List<Article> shopArticles = new ArrayList<>();

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
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        addBehaviour(receiveMessages);
        addBehaviour(new FindAgentBehaviour(this, 1000, agentFoundListener, ServiceType.BROKER));
        addBehaviour(new RegenerateShopSuppliesBehaviour(this, 10000, supplyGeneratedListener));
    }

    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        Object content = msg.getContentObject();

        if (content instanceof ArticlesStatusQuery) {
            logReceivedMessage(msg, ArticlesStatusQuery.class);
            handleArticlesStatusQuery(msg);
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    FindAgentBehaviour.AgentFoundListener agentFoundListener = agent -> {
        brokerAgent = agent;
        registerInBrokerAgent();
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

    private void registerInBrokerAgent() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.debug("register itself in {} agent", ServiceType.BROKER);
                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.INFORM);
                msg.addReceiver(brokerAgent);
                try {
                    msg.setContentObject(new RegisterShopAgent(myAgent.getName()));
                    send(msg);
                } catch (IOException ex) {
                    log.error("IOException in registerInBrokerAgent", ex);
                }
            }
        });
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

    private ArticlesStatusReply createArticlesStatusReply(ArticlesStatusQuery articlesStatusQuery) {
        log.trace("createArticlesStatusReply");
        List<Article> articlesToStatusReply = new ArrayList<>();
        articlesStatusQuery.getArticlesToBuy().forEach(atb ->
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
        return new ArticlesStatusReply(articlesToStatusReply);
    }
}
