package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public abstract class HandleRespond implements Serializable {
    protected Agent myAgent;
    private ACLMessage sendMessage;
    private Boolean isFinished;

    public HandleRespond(Agent agent, ACLMessage sendMessage) {
        this.myAgent = agent;
        this.sendMessage = sendMessage;
        isFinished = false;
    }

    protected abstract void action(ACLMessage msg) throws UnreadableException;

    public ACLMessage getSendMessage() {
        return sendMessage;
    }

    public void finished() {
        isFinished = true;
    }

    public Boolean isFinished() {
        return isFinished;
    }
}
