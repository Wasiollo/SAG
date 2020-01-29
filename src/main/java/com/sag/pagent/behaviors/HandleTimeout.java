package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@Slf4j
@Getter
public abstract class HandleTimeout implements Serializable {
    protected final Agent myAgent;
    private boolean finished = false;
    private TimeoutBehavior timeoutBehavior;

    public HandleTimeout(Agent myAgent) {
        this.myAgent = myAgent;
    }

    public HandleTimeout(Agent myAgent, Date timeoutDate) {
        this.myAgent = myAgent;
        setTimeout(timeoutDate);
    }

    public void setTimeout(Date timeoutDate) {
        if (timeoutDate == null) return;
        timeoutBehavior = new TimeoutBehavior(this.myAgent, timeoutDate);
        this.myAgent.addBehaviour(timeoutBehavior);
    }

    protected void onTimeout() {
        log.debug("Timeout !!!");
    }

    public void finished() {
        finished = true;
        if (timeoutBehavior != null) {
            this.myAgent.removeBehaviour(timeoutBehavior);
        }
    }

    private class TimeoutBehavior extends WakerBehaviour {
        TimeoutBehavior(Agent a, Date wakeupDate) {
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
