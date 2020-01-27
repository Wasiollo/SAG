package com.sag.pagent.manager.messages;

import com.sag.pagent.customer.order.OrderArticle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class PurchaseOrder implements Serializable {
    private final String customerAgentId;
    private final List<OrderArticle> articlesToBuy;
    private final Double budget;
}
