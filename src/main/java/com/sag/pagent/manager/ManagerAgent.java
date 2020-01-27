package com.sag.pagent.manager;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.manager.messages.PurchaseOrder;
import com.sag.pagent.services.ServiceType;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ManagerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceManager());
    }

    private ServiceDescription getServiceManager() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.MANAGER.getType();
        sd.setType(type);
        sd.setName(type);
        return sd;
    }

    @Override
    protected void setup() {
        super.setup();

        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        addBehaviour(receiveMessages);
    }

    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        Object content = msg.getContentObject();

        if (content instanceof PurchaseOrder) {
            logReceivedMessage(msg, PurchaseOrder.class);
            handlePurchaseOrder(msg);
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    private void handlePurchaseOrder(ACLMessage msg) throws UnreadableException {
        PurchaseOrder purchaseOrder = (PurchaseOrder) msg.getContentObject();
        log.info("Handling purchaseOrder from " + purchaseOrder.getCustomerAgentId());
        purchaseOrders.add(purchaseOrder);
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        send(reply);
        log.info("Purchase order handled");
    }
}
