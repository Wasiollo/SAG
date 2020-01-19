package com.sag.pagent.agents;

import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.messages.RegisterShopAgent;
import com.sag.pagent.services.ServiceType;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

@Slf4j
public class BrokerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private HashSet<String> registeredShopAgents;

    public BrokerAgent() {
        registeredShopAgents = new HashSet<>();
    }

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceBroker());
    }

    private ServiceDescription getServiceBroker() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.BROKER.getType();
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
        Object content = msg.getContentObject();

        if (content instanceof RegisterShopAgent) {
            log.debug("get message RegisterShopAgent");
            addBehaviour(new HandleRegisterShopAgent(this, msg));
        } else {
            receiveMessages.replyNotUnderstood(msg);
        }
    };

    private class HandleRegisterShopAgent extends OneShotBehaviour {
        private ACLMessage msg;

        public HandleRegisterShopAgent(Agent a, ACLMessage msg) {
            super(a);
            this.msg = msg;
        }

        @Override
        public void action() {
            try {
                RegisterShopAgent registerShopAgent = (RegisterShopAgent) msg.getContentObject();
                registeredShopAgents.add(registerShopAgent.shopAgentId);
            } catch (UnreadableException e) {
                log.error("Exception while handling RegisterShopAgent message", e);
            }
        }
    }
}
