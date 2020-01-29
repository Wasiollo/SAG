package com.sag.pagent.manager.messages;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class BuyProductsResponse implements Serializable {
    private final Integer boughtAmount;
    private final Double usedMoney;
    private final BuyProductsRequest request;
}
