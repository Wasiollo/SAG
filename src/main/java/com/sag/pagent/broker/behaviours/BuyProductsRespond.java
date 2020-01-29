package com.sag.pagent.broker.behaviours;

import com.sag.pagent.Constant;
import com.sag.pagent.behaviors.HandleManyMessagesRespond;
import com.sag.pagent.behaviors.HandleOneRespond;
import com.sag.pagent.broker.ShopArticle;
import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.shop.messages.PurchaseArticle;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class BuyProductsRespond extends HandleManyMessagesRespond {
    private final long TIMEOUT = Constant.BUY_PRODUCTS_TIME_TO_RESPOND;
    private final BuyProductsRequest buyProductsRequest;
    private final List<ShopArticle> shopArticleList;
    private final List<ACLMessage> messageList = new LinkedList<>();
    private final Date respondDate = new Date();
    private final List<HandleOneRespond> handleOneRespondList = new LinkedList<>();

    public BuyProductsRespond(Agent myAgent, BuyProductsRequest buyProductsRequest, List<ShopArticle> shopArticleList) {
        super(myAgent);
        this.buyProductsRequest = buyProductsRequest;
        this.shopArticleList = shopArticleList;
        respondDate.setTime(respondDate.getTime() + TIMEOUT);
        generateMessages();
    }

    private void generateMessages() {
        try {
            for (ShopArticle shopArticle : shopArticleList) {
                ACLMessage aclMessage = MessagesUtils.createMessage(ACLMessage.REQUEST);
                aclMessage.addReceiver(shopArticle.getShopAgent());
                aclMessage.setContentObject(new PurchaseArticle(shopArticle.getArticle()));
                aclMessage.setReplyByDate(respondDate);
                messageList.add(aclMessage);
                handleOneRespondList.add(addMessage(aclMessage));
            }
        } catch (IOException e) {
            log.error("Exception while serializing Article", e);
        }
    }

    public List<ACLMessage> getMessages() {
        return messageList;
    }

    public List<HandleOneRespond> getHandleOneRespondList() {
        return handleOneRespondList;
    }

    @Override
    public void onRespond(ACLMessage msg) {

    }

    @Override
    public void onRespondAll() {

    }
}
