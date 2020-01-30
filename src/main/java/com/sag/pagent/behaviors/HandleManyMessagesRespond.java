package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class HandleManyMessagesRespond extends HandleTimeout {
    private int messageCounter = 0;
    private int messageNumber = 0;

    public HandleManyMessagesRespond(Agent myAgent) {
        super(myAgent);
    }

    public abstract void onRespond(ACLMessage msg) throws UnreadableException;

    public abstract void onRespondAll();

    public HandleOneRespond addMessage(ACLMessage msg) {
        messageNumber++;
        return new HandleOneOfManyRespond(myAgent, msg);
    }

    private class HandleOneOfManyRespond extends HandleOneRespond {
        HandleOneOfManyRespond(Agent myAgent, ACLMessage sendMessage) {
            super(myAgent, sendMessage);
        }

        @Override
        protected void action(ACLMessage msg) throws UnreadableException {
            messageCounter++;
            log.debug("get message {}/{} from {} id {}", messageCounter, messageNumber, msg.getSender().getLocalName(), msg.getConversationId());
            onRespond(msg);
            if (messageCounter == messageNumber) {
                onRespondAll();
                HandleManyMessagesRespond.this.finished();
            }
            finished();
        }
    }
}
