package com.sag.pagent.customer;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.customer.order.OrderGenerator;
import com.sag.pagent.customer.order.OrderList;
import com.sag.pagent.customer.order.OrderListDispatcher;
import com.sag.pagent.services.ServiceType;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CustomerAgent extends BasicAgent {
    private static final Integer MAX_GENERATED_NEEDS = 200;
    private static final Double MAX_GENERATED_BUDGET = 2000d;
    private static final Double MIN_GENERATED_BUDGET = 1000d;

    private ReceiveMessagesBehaviour receiveMessages;
    private OrderList orderList;
    private OrderListDispatcher orderListDispatcher;
    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> {
        receiveMessages.replyNotUnderstood(msg);
    };

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

        orderList = new OrderGenerator(MAX_GENERATED_NEEDS, MIN_GENERATED_BUDGET, MAX_GENERATED_BUDGET).generate();
        orderListDispatcher = new OrderListDispatcher(this);
        sendPurchaseOrderToBrokerAgents(orderList);
    }

    private void sendPurchaseOrderToBrokerAgents(OrderList orderList) {
        List<ACLMessage> messageList = orderListDispatcher.dispatch(orderList);
        for (ACLMessage message : messageList) {
//            send(message);
        }
        log.debug("PurchaseOrders sent");
    }
}
