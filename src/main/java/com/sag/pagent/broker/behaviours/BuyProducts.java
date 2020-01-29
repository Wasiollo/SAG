package com.sag.pagent.broker.behaviours;

import com.sag.pagent.Constant;
import com.sag.pagent.behaviors.HandleManyMessagesRespond;
import com.sag.pagent.behaviors.HandleOneRespond;
import com.sag.pagent.broker.ShopArticle;
import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.shop.messages.PurchaseArticle;
import com.sag.pagent.shop.messages.PurchaseReport;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class BuyProducts extends HandleManyMessagesRespond {
    private final ACLMessage buyProductsRequestMessage;
    private final BuyProductsRequest buyProductsRequest;
    private final List<ShopArticle> shopArticleList;
    private final Map<String, PurchaseArticle> shopArticleMap = new HashMap<>();
    private final List<ACLMessage> messageList = new LinkedList<>();
    private final List<HandleOneRespond> handleOneRespondList = new LinkedList<>();
    private int boughtAmount = 0;
    private double usedMoney = 0;


    public BuyProducts(Agent myAgent, ACLMessage buyProductsRequestMessage, BuyProductsRequest buyProductsRequest,
                       List<ShopArticle> shopArticleList) {
        super(myAgent);
        this.buyProductsRequestMessage = buyProductsRequestMessage;
        this.buyProductsRequest = buyProductsRequest;
        this.shopArticleList = shopArticleList;

        Date respondDate = createRespondDate();
        generateMessages(respondDate);
        setTimeout(respondDate);
    }

    private Date createRespondDate() {
        Date respondDate = new Date();
        respondDate.setTime(respondDate.getTime() + Constant.BUY_PRODUCTS_TIME_TO_RESPOND);
        return respondDate;
    }

    private void generateMessages(Date respondDate) {
        try {
            for (ShopArticle shopArticle : shopArticleList) {
                ACLMessage aclMessage = MessagesUtils.createMessage(ACLMessage.REQUEST);
                aclMessage.addReceiver(shopArticle.getShopAgent());
                PurchaseArticle purchaseArticle = new PurchaseArticle(shopArticle.getArticle());
                aclMessage.setContentObject(purchaseArticle);
                aclMessage.setReplyByDate(respondDate);
                shopArticleMap.put(aclMessage.getConversationId(), purchaseArticle);
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
        try {
            PurchaseReport purchaseReport = (PurchaseReport) msg.getContentObject();
            boughtAmount += purchaseReport.getAmount();
            usedMoney += purchaseReport.getExpense();
            shopArticleMap.remove(msg.getConversationId());
        } catch (UnreadableException e) {
            log.error("Exception while serializing PurchaseReport", e);
        }
    }

    @Override
    protected void onTimeout() {
        super.onTimeout();
        shopArticleMap.values().forEach(purchaseArticle -> usedMoney += purchaseArticle.getBudget());
    }

    @Override
    public void onRespondAll() {
        try {
            ACLMessage reply = buyProductsRequestMessage.createReply();
            reply.setContentObject(new BuyProductsResponse(boughtAmount, usedMoney, buyProductsRequest));
            myAgent.send(reply);
        } catch (IOException e) {
            log.error("Exception while serializing BuyProductsResponse", e);
        }
    }
}