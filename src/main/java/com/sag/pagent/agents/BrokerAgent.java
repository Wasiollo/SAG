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

import java.util.HashSet;

public class BrokerAgent extends BasicAgent {
    private ReceiveMessages receiveMessages;
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
        receiveMessages = new ReceiveMessages(this);
        addBehaviour(receiveMessages);
    }

    private class ReceiveMessages extends ReceiveMessagesBehaviour {

        public ReceiveMessages(Agent a) {
            super(a);
        }

        @Override
        protected void receivedNewMessage(ACLMessage msg) throws UnreadableException {
            Object content = msg.getContentObject();

            if (content instanceof RegisterShopAgent) {
                log.debug("get message RegisterShopAgent");
                addBehaviour(new HandleRegisterShopAgent(myAgent, msg));
            } else {
                replyNotUnderstood(msg);
            }
        }

    }

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
