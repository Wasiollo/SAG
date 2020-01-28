package com.sag.pagent.manager;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.messages.MessagesUtils;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class BuyProductsDispatcher {
    public static ACLMessage prepareBuyProductsMessage(AID broker, BuyProductsRequest request) {
        ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
        msg.addReceiver(broker);
        try {
            msg.setContentObject(request);
        } catch (IOException e) {
            log.error("Error occurred during BuyProducts Message preparation", e);
        }
        log.debug("BuyProductsRequest prepared for broker: {}", broker.getLocalName());
        return msg;
    }
}
