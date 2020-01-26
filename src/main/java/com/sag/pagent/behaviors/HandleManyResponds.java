package com.sag.pagent.behaviors;

import com.google.common.collect.Iterators;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class HandleManyResponds extends HandleRespond {
    private final int expectedRequestResponds;
    private int requestRespondsCounter;

    public HandleManyResponds(Agent myAgent, ACLMessage sendMessage) {
        super(myAgent, sendMessage);
        expectedRequestResponds = Iterators.size(sendMessage.getAllReceiver());
        requestRespondsCounter = 0;
    }

    @Override
    protected void action(ACLMessage msg) throws UnreadableException {
        repeatAction(msg);
        increaseRequestRespondsCounter(msg);
        if (isReceivedExpectedRequestResponds()) {
            finished();
            afterFinished();
        }
    }

    @SuppressWarnings("unused")
    private void increaseRequestRespondsCounter(ACLMessage msg) {
        requestRespondsCounter++;
    }

    private boolean isReceivedExpectedRequestResponds() {
        log.debug("responds: {}/{}", requestRespondsCounter, expectedRequestResponds);
        return expectedRequestResponds == requestRespondsCounter;
    }

    protected void afterFinished() {
        log.debug("AfterFinished");
    }

    protected abstract void repeatAction(ACLMessage msg) throws UnreadableException;
}
