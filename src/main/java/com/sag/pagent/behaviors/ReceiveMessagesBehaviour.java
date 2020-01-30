package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;

@Slf4j
public class ReceiveMessagesBehaviour extends CyclicBehaviour {
    private ReceiveMessageListener receiveMessageListener;
    private HashMap<String, HandleOneRespond> respondMap;

    public interface ReceiveMessageListener extends Serializable {
        void receivedNewMessage(ACLMessage msg) throws UnreadableException;
    }

    public ReceiveMessagesBehaviour(Agent a, ReceiveMessageListener receiveMessageListener) {
        super(a);
        this.receiveMessageListener = receiveMessageListener;
        respondMap = new HashMap<>();
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg == null) {
            block();
            return;
        }
        parseMessage(msg);
    }

    private void parseMessage(ACLMessage msg) {
        try {
            if (isKnownMessage(msg)) {
                receivedKnownMessage(msg);
            } else if (msg.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
                log.error("received NOT_UNDERSTOOD messaged with not know conversationId {}", msg.getConversationId());
            } else {
                receiveMessageListener.receivedNewMessage(msg);
            }
        } catch (UnreadableException ue){
            log.error("Message could not be parsed", ue);
            replyNotUnderstood(msg);
        } catch (Exception ex) {
            log.error("Exception occurred parsing Message", ex);
        }
    }

    private boolean isKnownMessage(ACLMessage msg) {
        return respondMap.containsKey(msg.getConversationId());
    }

    private void receivedKnownMessage(ACLMessage msg) throws UnreadableException {
        HandleOneRespond handleOneRespond = respondMap.get(msg.getConversationId());
        handleOneRespond.action(msg);
        if (Boolean.TRUE.equals(handleOneRespond.isFinished())) {
            unregisterRespond(msg.getConversationId());
        }
    }

    public void registerRespond(HandleOneRespond handleOneRespond) {
        respondMap.put(handleOneRespond.getConversationId(), handleOneRespond);
    }

    public void unregisterRespond(String conversationId) {
        respondMap.remove(conversationId);
    }

    public void replyNotUnderstood(ACLMessage msg) {
        log.error("Send NotUnderstood message with conversationId: {} to: {}", msg.getConversationId(), msg.getSender().getLocalName());
        try {
            java.io.Serializable content = msg.getContentObject();
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            reply.setContentObject(content);
            myAgent.send(reply);
        } catch (Exception ex) {
            log.error("Exception occurred during reply for not understood message", ex);
        }
    }
}
