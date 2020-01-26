package com.sag.pagent.customer;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.HandleRespond;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.customer.order.OrderGenerator;
import com.sag.pagent.customer.order.OrderList;
import com.sag.pagent.customer.order.OrderListDispatcher;
import com.sag.pagent.services.ServiceType;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
public class CustomerAgent extends BasicAgent {
    private static final Integer MAX_GENERATED_NEEDS = 200;
    private static final Double MAX_GENERATED_BUDGET = 2000d;
    private static final Double MIN_GENERATED_BUDGET = 1000d;
    private static final Long TIME_TO_RESPOND = 5L * 1000;

    private ReceiveMessagesBehaviour receiveMessages;
    private OrderListDispatcher orderListDispatcher;
    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> receiveMessages.replyNotUnderstood(msg);

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

        OrderList orderList = new OrderGenerator(MAX_GENERATED_NEEDS, MIN_GENERATED_BUDGET, MAX_GENERATED_BUDGET).generate();
        orderListDispatcher = new OrderListDispatcher(this);
        sendPurchaseOrderToBrokerAgents(orderList);
    }

    private void sendPurchaseOrderToBrokerAgents(OrderList orderList) {
        List<ACLMessage> messageList = orderListDispatcher.dispatch(orderList);
        Date respondDate = new Date();
        respondDate.setTime(respondDate.getTime() + TIME_TO_RESPOND);
        for (ACLMessage message : messageList) {
            message.setReplyByDate(respondDate);
            send(message);
            receiveMessages.registerRespond(new PurchaseOrderRespond(this, message));
        }
        log.debug("PurchaseOrders sent");
    }

    private class PurchaseOrderRespond extends HandleRespond {
        public PurchaseOrderRespond(Agent myAgent, ACLMessage sendMessage) {
            super(myAgent, sendMessage);
        }

        @Override
        protected void action(ACLMessage msg) {
            log.debug("Respond received from: {}", msg.getSender().getLocalName());
            finished();
        }

        @Override
        protected void onTimeout() {
            log.debug("Timeout for respond: {} from {}", getConversationId(), getFirstReceiver().getLocalName());
            orderListDispatcher.killBroker(getFirstReceiver());
            OrderList orderList = orderListDispatcher.getOrderList(getFirstReceiver(), getConversationId());
            sendPurchaseOrderToBrokerAgents(orderList);
        }
    }
}
