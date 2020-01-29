package com.sag.pagent.broker.behaviours;

import com.sag.pagent.behaviors.HandleManyMessagesRespond;
import com.sag.pagent.broker.ShopArticle;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class BuyProductsRespond extends HandleManyMessagesRespond {
    private final List<ShopArticle> shopArticleList;

    public BuyProductsRespond(Agent myAgent, List<ShopArticle> shopArticleList) {
        super(myAgent);
        this.shopArticleList = shopArticleList;
    }

    public List<ACLMessage> getMessages() {

    }

    @Override
    public void onRespond(ACLMessage msg) {

    }

    @Override
    public void onRespondAll() {

    }
}
