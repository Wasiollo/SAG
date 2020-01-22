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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class CustomerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private List<Article> customerNeeds = new ArrayList<>();
    private List<PurchaseOrder> orders = new ArrayList<>();
    private static final Integer MAX_GENERATED_NEEDS = 200;
    private static final Double MAX_GENERATED_BUDGET = 200d;
    private static final Double MIN_GENERATED_BUDGET = 100d;

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
                sendPurchaseOrderToBrokerAgents();
                log.debug("Purchase Orders sent");
            }
        });
    }

    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        receiveMessages.replyNotUnderstood(msg);
    };

    private Double chooseBudget() {
        double budget = ThreadLocalRandom.current().nextDouble(MIN_GENERATED_BUDGET, MAX_GENERATED_BUDGET + 1);
        return ((double) ((int) (budget * 100.0))) / 100.0;
    }

    private List<Article> chooseArticlesToPurchase() {
        List<Article> randomlyChosenArticles = customerNeeds.stream()
                .filter(n -> new Random().nextBoolean())
                .map(a -> Article.builder()
                        .name(a.getName())
                        .price(a.getPrice())
                        .amount(a.getAmount())
                        .build()
                )
                .collect(Collectors.toList());

        randomlyChosenArticles.forEach(article -> article.setAmount(ThreadLocalRandom.current().nextInt(1, article.getAmount() + 1)));

        removeChosenArticlesFromNeeds(randomlyChosenArticles);

        return randomlyChosenArticles;
    }

    private void removeChosenArticlesFromNeeds(List<Article> randomlyChosenArticles) {
        for(Article need: customerNeeds){
            randomlyChosenArticles.stream().filter(a -> a.getName().equals(need.getName()))
                    .findAny()
                    .ifPresent(a -> need.minusAmount(a.getAmount()));
        }
        customerNeeds.removeIf(article -> article.getAmount() == 0);
    }

    private void sendPurchaseOrderToBrokerAgents() {
        log.debug("sending purchaseOrders");
        try {
            List<AID> brokers = ServiceUtils.findAgentList(this, ServiceType.BROKER);
            int i = 0;
            while (!customerNeeds.isEmpty()) {
                PurchaseOrder order = new PurchaseOrder(
                        getName(),
                        chooseArticlesToPurchase(),
                        chooseBudget()
                );
                orders.add(order);
                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
                AID brokerToSend = brokers.get(++i % brokers.size());
                msg.addReceiver(brokerToSend);
                msg.setContentObject(order);
                send(msg);
                log.debug("Purchase Order {} sent to: {}", i, brokerToSend.getLocalName());
            }
            log.debug("SENT");
        } catch (IOException ex) {
            log.error("Error occured in sendPurchaseOrderToBrokerAgents", ex);
        }
    }
}
