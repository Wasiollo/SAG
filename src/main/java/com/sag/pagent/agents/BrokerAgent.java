package com.sag.pagent.agents;

import com.sag.pagent.behaviors.HandleManyResponds;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.messages.*;
import com.sag.pagent.services.ServiceType;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BrokerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private HashSet<AID> registeredShopAgents;

    public BrokerAgent() {
        registeredShopAgents = new HashSet<>();
    }

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceBroker());
    }

    private ServiceDescription getServiceBroker() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.BROKER.getType();
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

    ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        Object content = msg.getContentObject();

        if (content instanceof RegisterShopAgent) {
            logReceivedMessage(msg, RegisterShopAgent.class);
            handleRegisterShopAgent(msg);
        } else if (content instanceof PurchaseOrder) {
            logReceivedMessage(msg, PurchaseOrder.class);
            handlePurchaseOrder(msg);
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    private void handleRegisterShopAgent(ACLMessage msg) {
        try {
            RegisterShopAgent registerShopAgent = (RegisterShopAgent) msg.getContentObject();
            registeredShopAgents.add(new AID(registerShopAgent.getShopAgentName(), AID.ISGUID));
        } catch (UnreadableException e) {
            log.error("Exception while handling RegisterShopAgent message", e);
        }
    }

    /**
     * TODO: Register PurchaseOrder
     */
    @SuppressWarnings("unused")
    private void handlePurchaseOrder(ACLMessage msg) {
        sendArticlesStatusQuery(new ArticlesStatusQuery());
    }

    private void sendArticlesStatusQuery(@Nonnull ArticlesStatusQuery articlesStatusQuery) {
        try {
            List<String> agentNameList = registeredShopAgents.stream().map(AID::getLocalName).collect(Collectors.toList());
            log.debug("send ArticlesStatusQuery to {}", agentNameList);
            ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
            for (AID agent : registeredShopAgents) {
                msg.addReceiver(agent);
            }
            msg.setContentObject(articlesStatusQuery);
            send(msg);
            receiveMessages.registerRespond(new HandleArticlesStatusReplies(this, msg));
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private class HandleArticlesStatusReplies extends HandleManyResponds {
        public HandleArticlesStatusReplies(Agent myAgent, ACLMessage sendMessage) {
            super(myAgent, sendMessage);
        }

        @Override
        protected void repeatAction(ACLMessage msg) throws UnreadableException {
            logReceivedMessage(msg, ArticlesStatusReply.class);
            ArticlesStatusReply articlesStatusReply = (ArticlesStatusReply) msg.getContentObject();
            updatePurchaseOrderStatus(articlesStatusReply);
        }

        /**
         * TODO: Start talk with other BrokeAgent who should buy what
         */
        @Override
        protected void afterFinished(ACLMessage msg) {
            log.debug("afterFinished");
        }
    }

    /**
     * TODO: Update PurchaseOrder. Who have the lowest price etc.
     */
    @SuppressWarnings("unused")
    private void updatePurchaseOrderStatus(ArticlesStatusReply articlesStatusReply) {
        log.trace("updatePurchaseOrderStatus");
    }
}
