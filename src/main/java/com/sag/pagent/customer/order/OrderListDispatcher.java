package com.sag.pagent.customer.order;

import com.sag.pagent.manager.messages.PurchaseOrder;
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
    private Map<AID, Boolean> isManagerAlive = new HashMap<>();
    private Map<AID, Map<String, OrderList>> managerToOrderListMap = new HashMap<>();

    public OrderListDispatcher(Agent agent) {
        this.agent = agent;
        List<AID> managerAgents = ServiceUtils.findAgentList(agent, ServiceType.MANAGER);
        for (AID manager : managerAgents) {
            isManagerAlive.put(manager, true);
            managerToOrderListMap.put(manager, new HashMap<>());
        }
    }

    public List<ACLMessage> dispatch(OrderList orderList) {
        List<AID> aliveManagerList = getAliveManagerList();
        if (aliveManagerList.isEmpty()) {
            log.error("No managerAgent is alive");
            return new LinkedList<>();
        }

        List<OrderList> splitOrderList = orderList.splitOrder(aliveManagerList.size());

        Iterator<AID> aliveManagerIt = aliveManagerList.iterator();
        Iterator<OrderList> splitOrderIt = splitOrderList.iterator();

        List<ACLMessage> aclMessageList = new LinkedList<>();
        while (aliveManagerIt.hasNext()) {
            ACLMessage msg = prepareMessage(aliveManagerIt.next(), splitOrderIt.next());
            if (msg == null) continue;
            aclMessageList.add(msg);
        }

        return aclMessageList;
    }

    private List<AID> getAliveManagerList() {
        return isManagerAlive.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Nullable
    private ACLMessage prepareMessage(AID manager, OrderList order) {
        ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
        msg.addReceiver(manager);
        try {
            msg.setContentObject(createPurchaseOrder(order));
        } catch (IOException e) {
            log.error("Error on setContentObject", e);
            return null;
        }
        managerToOrderListMap.get(manager).put(msg.getConversationId(), order);
        log.debug("Purchase Order prepared for manager: {}", manager.getLocalName());
        return msg;
    }

    private PurchaseOrder createPurchaseOrder(OrderList orderList) {
        return new PurchaseOrder(
                agent.getName(),
                orderList.getOrderArticles(),
                orderList.getRemainingBudget()
        );
    }

    public void killManager(AID managerAgent) {
        log.debug("Kill manager: {}", managerAgent.getLocalName());
        isManagerAlive.put(managerAgent, false);
    }

    public OrderList getOrderList(AID managerAgent, String conversationId) {
        return managerToOrderListMap.get(managerAgent).get(conversationId);
    }
}
