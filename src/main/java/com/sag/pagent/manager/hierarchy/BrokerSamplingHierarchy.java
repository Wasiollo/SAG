package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import jade.core.AID;

public class BrokerSamplingHierarchy extends BrokerHierarchy {

    @Override
    public void updateHierarchy(AID sender, BuyProductsResponse response, BuyProductsRequest buyProductsRequest) {

    }
}
