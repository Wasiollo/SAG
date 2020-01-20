package com.sag.pagent.messages;

import com.sag.pagent.shop.domain.Article;
import jade.core.AID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class PurchaseOrder implements Serializable {
    private final String customerAgentId;
    private final LinkedList<String> brokerAgentNameList;
    private final List<Article> articlesToBuy;
    private final String uid;

    public PurchaseOrder(String customerAgentId, List<AID> brokerAgentIdList, List<Article> articlesToBuy) {
        this(customerAgentId, brokerAgentIdList.stream()
                        .map(AID::getName)
                        .collect(Collectors.toCollection(LinkedList::new)),
                articlesToBuy,
                MessagesUtils.generateRandomStringByUUIDNoDash());
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
