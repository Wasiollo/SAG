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
    private final List<Article> articlesToBuy;
    private final String uid;
    private final Double budget;

    public PurchaseOrder(String customerAgentId, List<Article> articlesToBuy, Double budget) {
        this(customerAgentId,
                articlesToBuy,
                MessagesUtils.generateRandomStringByUUIDNoDash(),
                budget);
    }
}
