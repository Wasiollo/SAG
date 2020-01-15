package com.sag.pagent.agents;

import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.messages.RegisterShopAgent;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public class ShopAgent extends BasicAgent {
    ReceiveMessages receiveMessages;
    AID brokerAgent;

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
        addBehaviour(new FindBrokerAgent(this, 1000));
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

    public class FindBrokerAgent extends TickerBehaviour {
        public FindBrokerAgent(Agent a, long timeout) {
            super(a, timeout);
        }

        @Override
        protected void onTick() {
            brokerAgent = ServiceUtils.findAgent(myAgent, ServiceType.BROKER);

            if (brokerAgent == null) {
                log.debug("{} agent not found", ServiceType.BROKER);
                return;
            }
            log.debug("{} agent found", ServiceType.BROKER);
            removeBehaviour(this);
            registerInBrokerAgent();
        }
    }

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
