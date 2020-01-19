package com.sag.pagent.agents;

import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.messages.PurchaseOrder;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.IOException;

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
        addBehaviour(new WakerBehaviour(this, 100) {
            @Override
            protected void onWake() {
                sendPurchaseOrderToBrokerAgents(createPurchaseOrder());
            }
        });
    }

    ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        receiveMessages.replyNotUnderstood(msg);
    };

    private PurchaseOrder createPurchaseOrder() {
        log.trace("createPurchaseOrder");
        return new PurchaseOrder(
                getName(),
                ServiceUtils.findAgentList(this, ServiceType.BROKER)
        );
    }

    private void sendPurchaseOrderToBrokerAgents(@Nonnull PurchaseOrder purchaseOrder) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    log.debug("send purchaseOrder to {}", purchaseOrder.getBrokerAgentLocalNameList());
                    ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
                    for (AID agent : purchaseOrder.getBrokerAgentIdList()) {
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
