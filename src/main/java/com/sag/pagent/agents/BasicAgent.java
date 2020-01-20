package com.sag.pagent.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BasicAgent extends Agent {

    @Override
    protected void setup() {
        log.info("start");
        registerServices();
    }

    private void registerServices() {
        DFAgentDescription dfd = createDFDescriptor();
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            log.error("FIPAException in registerService", fe);
        }
    }

    private DFAgentDescription createDFDescriptor() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        addServices(dfd);
        return dfd;
    }

    protected abstract void addServices(DFAgentDescription dfd);

    @Override
    protected void takeDown() {
        deregisterServices();
        log.info("stop");
    }

    private void deregisterServices() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            log.error("FIPAException in deregisterService", fe);
        }
    }

    protected void logReceivedMessage(ACLMessage msg, final Class<?> clazz) {
        log.debug("get message {} from {}", clazz.getSimpleName(), msg.getSender().getLocalName());
    }
}
