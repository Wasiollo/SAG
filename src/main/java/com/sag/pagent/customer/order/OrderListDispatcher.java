package com.sag.pagent.customer.order;

import com.sag.pagent.customer.messages.PurchaseOrder;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class OrderListDispatcher implements Serializable {
    private Agent agent;
    private Map<AID, Boolean> isBrokerAlive = new HashMap<>();
    private Map<AID, Map<String, OrderList>> brokerToOrderListMap = new HashMap<>();

    public OrderListDispatcher(Agent agent) {
        this.agent = agent;
        List<AID> brokerAgents = ServiceUtils.findAgentList(agent, ServiceType.BROKER);
        for (AID broker : brokerAgents) {
            isBrokerAlive.put(broker, true);
            brokerToOrderListMap.put(broker, new HashMap<>());
        }
    }

    public List<ACLMessage> dispatch(OrderList orderList) {
        List<AID> aliveBrokerList = getAliveBrokerList();
        if (aliveBrokerList.isEmpty()) {
            log.error("No brokerAgent is alive");
            return new LinkedList<>();
        }

        List<OrderList> splitOrderList = orderList.splitOrder(aliveBrokerList.size());

        Iterator<AID> aliveBrokerIt = aliveBrokerList.iterator();
        Iterator<OrderList> splitOrderIt = splitOrderList.iterator();

        List<ACLMessage> aclMessageList = new LinkedList<>();
        while (aliveBrokerIt.hasNext()) {
            ACLMessage msg = prepareMessage(aliveBrokerIt.next(), splitOrderIt.next());
            if (msg == null) continue;
            aclMessageList.add(msg);
        }

        return aclMessageList;
    }

    private List<AID> getAliveBrokerList() {
        return isBrokerAlive.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Nullable
    private ACLMessage prepareMessage(AID broker, OrderList order) {
        ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
        msg.addReceiver(broker);
        try {
            msg.setContentObject(createPurchaseOrder(order));
        } catch (IOException e) {
            log.error("Error on setContentObject", e);
            return null;
        }
        brokerToOrderListMap.get(broker).put(msg.getConversationId(), order);
        log.debug("Purchase Order prepared for broker: {}", broker.getLocalName());
        return msg;
    }

    private PurchaseOrder createPurchaseOrder(OrderList orderList) {
        return new PurchaseOrder(
                agent.getName(),
                orderList.getOrderArticles(),
                orderList.getRemainingBudget()
        );
    }

    public void killBroker(AID brokerAgent) {
        log.debug("Kill broker: {}", brokerAgent.getLocalName());
        isBrokerAlive.put(brokerAgent, false);
    }

    public OrderList getOrderList(AID brokerAgent, String conversationId) {
        return brokerToOrderListMap.get(brokerAgent).get(conversationId);
    }
}
