package com.sag.pagent.behaviors;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class HandleOneRespond extends HandleTimeout {
    private final ACLMessage sendMessage;

    public HandleOneRespond(Agent myAgent, ACLMessage sendMessage) {
        super(myAgent);
        this.sendMessage = sendMessage;
        setTimeout(sendMessage.getReplyByDate());
    }

    protected abstract void action(ACLMessage msg) throws UnreadableException;

    @Override
    protected void onTimeout() {
        log.debug("Timeout for respond: {} from {}", getConversationId(), getFirstReceiver().getLocalName());
    }

    public String getConversationId() {
        return getSendMessage().getConversationId();
    }

    public AID getFirstReceiver() {
        return (AID) getSendMessage().getAllReceiver().next();
    }
}
