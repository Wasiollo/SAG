package com.sag.pagent.messages;

import jade.core.AID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class PurchaseOrder implements java.io.Serializable {
    private final String customerAgentId;
    private final LinkedList<String> brokerAgentNameList;

    public PurchaseOrder(String customerAgentId, List<AID> brokerAgentIdList) {
        this(
                customerAgentId,
                brokerAgentIdList.stream()
                        .map(AID::getName)
                        .collect(Collectors.toCollection(LinkedList::new)));
    }

    public List<AID> getBrokerAgentIdList() {
        return brokerAgentNameList.stream()
                .map(agentName -> new AID(agentName, AID.ISGUID))
                .collect(Collectors.toList());
    }

    public List<String> getBrokerAgentLocalNameList() {
        return brokerAgentNameList.stream()
                .map(agentName -> new AID(agentName, AID.ISGUID))
                .map(AID::getLocalName)
                .collect(Collectors.toList());
    }
}
