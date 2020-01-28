package com.sag.pagent.manager.messages;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import lombok.Data;

@Data
public class BuyProductsResponse {
    private final Integer boughtAmount;
    private final Double usedMoney;
    private final BuyProductsRequest request;

}
