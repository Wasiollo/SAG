package com.sag.pagent.agents;

import com.sag.pagent.behaviors.FindAgentBehaviour;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.messages.RegisterShopAgent;
import com.sag.pagent.services.ServiceType;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public class ShopAgent extends BasicAgent {
    private ReceiveMessages receiveMessages;
    private AID brokerAgent;

    @Override
    protected void addServices(DFAgentDescription dfd) {
        dfd.addServices(getServiceShop());
    }

    private ServiceDescription getServiceShop() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.SHOP.getType();
        sd.setType(type);
        sd.setName(type);
        return sd;
    }

    @Override
    protected void setup() {
        super.setup();
        receiveMessages = new ReceiveMessages(this);
        addBehaviour(receiveMessages);
        addBehaviour(new FindAgentBehaviour(this, 1000, agentFoundListener, ServiceType.BROKER));
    }

    private class ReceiveMessages extends ReceiveMessagesBehaviour {

        public ReceiveMessages(Agent a) {
            super(a);
        }

        @Override
        protected void receivedNewMessage(ACLMessage msg) throws UnreadableException {
            replyNotUnderstood(msg);
        }

    }

    FindAgentBehaviour.AgentFoundListener agentFoundListener = agent -> {
        brokerAgent = agent;
        registerInBrokerAgent();
    };

    private void registerInBrokerAgent() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.debug("register itself in {} agent", ServiceType.BROKER);
                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.INFORM);
                msg.addReceiver(brokerAgent);
                try {
                    msg.setContentObject(new RegisterShopAgent(myAgent.getName()));
                    send(msg);
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        });
    }
}
