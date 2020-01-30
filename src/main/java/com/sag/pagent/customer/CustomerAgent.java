package com.sag.pagent.customer;

import com.sag.pagent.agents.BasicAgent;
import com.sag.pagent.behaviors.HandleOneRespond;
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

import static com.sag.pagent.Constant.*;

@Slf4j
public class CustomerAgent extends BasicAgent {
    private ReceiveMessagesBehaviour receiveMessages;
    private OrderListDispatcher orderListDispatcher;
    private OrderList orderList;
    private ReceiveMessagesBehaviour.ReceiveMessageListener receiveMessageListener = msg -> receiveMessages.replyNotUnderstood(msg);

    public CustomerAgent() {
        receiveMessages = new ReceiveMessagesBehaviour(this, receiveMessageListener);
        orderListDispatcher = new OrderListDispatcher(this);
        orderList = new OrderGenerator(MAX_GENERATED_NEEDS, MIN_GENERATED_BUDGET, MAX_GENERATED_BUDGET).generate();
    }

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
        addBehaviour(receiveMessages);
        orderListDispatcher.updateManagerAgents();
        sendPurchaseOrderToManagerAgents(orderList);
    }

    private void sendPurchaseOrderToManagerAgents(OrderList orderList) {
        List<ACLMessage> messageList = orderListDispatcher.dispatch(orderList);
        Date respondDate = new Date();
        respondDate.setTime(respondDate.getTime() + PURCHASE_ORDER_TIME_TO_RESPOND);
        for (ACLMessage message : messageList) {
            message.setReplyByDate(respondDate);
            send(message);
            receiveMessages.registerRespond(new PurchaseOrderRespond(this, message));
        }
        log.debug("PurchaseOrders sent");
    }

    private class PurchaseOrderRespond extends HandleOneRespond {
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
            super.onTimeout();
            orderListDispatcher.killManager(getFirstReceiver());
            OrderList resendOrderList = orderListDispatcher.getOrderList(getFirstReceiver(), getConversationId());
            sendPurchaseOrderToManagerAgents(resendOrderList);
        }
    }
}
