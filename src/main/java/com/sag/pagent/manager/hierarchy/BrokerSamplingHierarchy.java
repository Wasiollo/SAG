package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.manager.PurchaseOrderManager;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import jade.core.AID;

public class BrokerSamplingHierarchy extends BrokerHierarchy {
    public BrokerSamplingHierarchy(PurchaseOrderManager purchaseOrderManager) {
        super(purchaseOrderManager);
    }

    @Override
    public void updateHierarchy(AID broker, BuyProductsResponse response, BuyProductsRequest buyProductsRequest) {
        checkIfBrokerBoughtSth(broker, response);
    }

    private void checkIfBrokerBoughtSth(AID broker, BuyProductsResponse response) {
        setBrokerBoughtAny(broker, response.getRequest().getArticleType(), response.getBoughtAmount() != 0);
    }
}
