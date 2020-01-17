package com.sag.pagent.agents;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;

public abstract class BasicAgent extends LoggerAgent {

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
            log.error(fe.getMessage());
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
            log.error(fe.getMessage());
        }
    }
}