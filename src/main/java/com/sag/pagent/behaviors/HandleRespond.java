package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Getter
public abstract class HandleRespond implements Serializable {
    protected final Agent myAgent;
    private final ACLMessage sendMessage;
    private boolean finished = false;

    protected abstract void action(ACLMessage msg) throws UnreadableException;

    public void finished() {
        finished = true;
    }
}
