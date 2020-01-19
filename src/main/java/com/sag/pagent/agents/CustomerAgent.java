package com.sag.pagent.agents;

import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.services.ServiceType;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceCustomer());
    }

    private ServiceDescription getServiceCustomer() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.CUSTOMER.getType();
        sd.setType(type);
        sd.setName(type);
        return sd;
    }

    @Override
    protected void setup() {
        super.setup();
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        addBehaviour(receiveMessages);
    }

    ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        receiveMessages.replyNotUnderstood(msg);
    };
}
