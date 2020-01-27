package com.sag.pagent.manager;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.services.ServiceType;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ManagerAgent extends BasicAgent {

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceManager());
    }

    private ServiceDescription getServiceManager() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.MANAGER.getType();
        sd.setType(type);
        sd.setName(type);
        return sd;
    }

    @Override
    protected void setup() {
        super.setup();
    }
}
