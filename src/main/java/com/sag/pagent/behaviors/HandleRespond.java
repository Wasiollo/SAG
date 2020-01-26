package com.sag.pagent.behaviors;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@Slf4j
@Getter
public abstract class HandleRespond implements Serializable {
    protected final Agent myAgent;
    private final ACLMessage sendMessage;
    private boolean finished = false;
    private TimeoutBehavior timeoutBehavior;

    public HandleRespond(Agent myAgent, ACLMessage sendMessage) {
        this.myAgent = myAgent;
        this.sendMessage = sendMessage;
        Date timeoutDate = sendMessage.getReplyByDate();
        if (timeoutDate != null) {
            timeoutBehavior = new TimeoutBehavior(this.myAgent, timeoutDate);
            this.myAgent.addBehaviour(timeoutBehavior);
        }
    }

    protected abstract void action(ACLMessage msg) throws UnreadableException;

    protected void onTimeout() {
        log.debug("Timeout for respond: {}", sendMessage.getConversationId());
    }

    public void finished() {
        finished = true;
        if (timeoutBehavior != null) {
            this.myAgent.removeBehaviour(timeoutBehavior);
        }
    }

    public String getConversationId() {
        return getSendMessage().getConversationId();
    }

    public AID getFirstReceiver() {
        return (AID) getSendMessage().getAllReceiver().next();
    }

    private class TimeoutBehavior extends WakerBehaviour {
        public TimeoutBehavior(Agent a, Date wakeupDate) {
            super(a, wakeupDate);
        }

        @Override
        protected void onWake() {
            super.onWake();
            onTimeout();
            timeoutBehavior = null;
            finished();
        }
    }
}
