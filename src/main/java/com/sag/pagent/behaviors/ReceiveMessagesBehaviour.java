package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;

public class ReceiveMessagesBehaviour extends CyclicBehaviour {
    protected final transient Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    private ReceiveMessageListener receiveMessageListener;
    private HashMap<String, HandleRespond> respondMap;

    public interface ReceiveMessageListener extends Serializable {
        public void receivedNewMessage(ACLMessage msg) throws UnreadableException;
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
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private boolean isKnownMessage(ACLMessage msg) {
        return respondMap.containsKey(msg.getConversationId());
    }

    private void receivedKnownMessage(ACLMessage msg) throws UnreadableException {
        HandleRespond handleRespond = respondMap.get(msg.getConversationId());
        handleRespond.action(msg);
        if (Boolean.TRUE.equals(handleRespond.isFinished())) {
            unregisterRespond(msg.getConversationId());
        }
    }

    public void registerRespond(HandleRespond handleRespond) {
        respondMap.put(handleRespond.getSendMessage().getConversationId(), handleRespond);
    }

    public void unregisterRespond(String conversationId) {
        respondMap.remove(conversationId);
    }

    public void replyNotUnderstood(ACLMessage msg) {
        log.error("NotUnderstood message with conversationId: {}", msg.getConversationId());
        try {
            java.io.Serializable content = msg.getContentObject();
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            reply.setContentObject(content);
            myAgent.send(reply);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
