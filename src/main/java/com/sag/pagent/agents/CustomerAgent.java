package com.sag.pagent.agents;

import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.messages.PurchaseOrder;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

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

    @SuppressWarnings("unused")
    private void sendArticleListToBrokerAgents(@Nonnull PurchaseOrder purchaseOrder) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    log.debug("register itself in {} agent", ServiceType.BROKER);
                    ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
                    List<AID> agents = ServiceUtils.findAgentList(myAgent, ServiceType.BROKER);
                    for (AID agent : agents) {
                        msg.addReceiver(agent);
                    }
                    msg.setContentObject(purchaseOrder);
                    send(msg);
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        });
    }
}
