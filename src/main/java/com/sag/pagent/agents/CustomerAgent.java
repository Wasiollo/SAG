package com.sag.pagent.agents;

import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.messages.PurchaseOrder;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import com.sag.pagent.shop.domain.Article;
import com.sag.pagent.shop.service.ArticleService;
import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class CustomerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private List<Article> customerNeeds = new ArrayList<>();
    private List<Article> needsSentToPurchase = new ArrayList<>();
    private static final Integer MAX_GENERATED_NEEDS = 10;

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceCustomer());
    }

    private ServiceDescription getServiceCustomer() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.CUSTOMER.getType();
        sd.setType(type);
        sd.setName(type);
        return sd;
    }

    @Override
    protected void setup() {
        super.setup();
        customerNeeds = ArticleService.generateClientNeeds(MAX_GENERATED_NEEDS);
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        addBehaviour(receiveMessages);
        addBehaviour(new WakerBehaviour(this, 100) {
            @Override
            protected void onWake() {
                sendPurchaseOrderToBrokerAgents(createPurchaseOrder());
            }
        });
    }

    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        receiveMessages.replyNotUnderstood(msg);
    };

    private PurchaseOrder createPurchaseOrder() {
        return new PurchaseOrder(
                getName(),
                ServiceUtils.findAgentList(this, ServiceType.BROKER),
                chooseArticlesToPurchase()
        );
    }

    private List<Article> chooseArticlesToPurchase() {
        List<Article> needsDiff = getDiffBetweenNeedsAndSent();
        List<Article> randomlyChosenArticles = needsDiff.stream()
                .filter(n -> new Random().nextBoolean())
                .peek(n -> n.setAmount(ThreadLocalRandom.current().nextInt(0, n.getAmount() + 1)))
                .collect(Collectors.toList());

        addArticlesToPurchaseToSentList(randomlyChosenArticles);

        return randomlyChosenArticles;
    }

    private void addArticlesToPurchaseToSentList(List<Article> randomlyChosenArticles) {
        List<Article> swapList = new ArrayList<>();
        randomlyChosenArticles.forEach(article -> {
            needsSentToPurchase.stream()
                    .filter(ntp -> ntp.getName().equals(article.getName()))
                    .findAny()
                    .ifPresent(foundArticle -> article.addAmount(foundArticle.getAmount()));
            swapList.add(article);
        });
        needsSentToPurchase = swapList;
    }

    private List<Article> getDiffBetweenNeedsAndSent() {
        List<Article> diffArticleList = new ArrayList<>();
        customerNeeds.forEach(cn -> {
            Optional<Article> sentArticle = needsSentToPurchase.stream()
                    .filter(ntp -> ntp.getName().equals(cn.getName()))
                    .findAny();
            if (sentArticle.isPresent()) {
                if (sentArticle.get().getAmount() < cn.getAmount()) {
                    diffArticleList.add(
                            Article.builder()
                                    .name(cn.getName())
                                    .amount(cn.getAmount() - sentArticle.get().getAmount())
                                    .build()
                    );
                }
            } else {
                diffArticleList.add(cn);
            }
        });
        return diffArticleList;
    }

    private void sendPurchaseOrderToBrokerAgents(@Nonnull PurchaseOrder purchaseOrder) {
        log.debug("send purchaseOrder to {}", purchaseOrder.getBrokerAgentLocalNameList());
        try {
            ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
            for (AID agent : purchaseOrder.getBrokerAgentIdList()) {
                msg.addReceiver(agent);
            }
            msg.setContentObject(purchaseOrder);
            send(msg);
        } catch (IOException ex) {
            log.error("Error occured in sendPurchaseOrderToBrokerAgents", ex);
        }
    }
}
