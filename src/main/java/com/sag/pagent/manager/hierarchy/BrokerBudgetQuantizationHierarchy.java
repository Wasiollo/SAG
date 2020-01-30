package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.manager.PurchaseOrderManager;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import jade.core.AID;

public class BrokerBudgetQuantizationHierarchy extends BrokerHierarchy {
    public BrokerBudgetQuantizationHierarchy(PurchaseOrderManager purchaseOrderManager) {
        super(purchaseOrderManager);
    }

    @Override
    public void updateHierarchy(AID sender, BuyProductsResponse response, BuyProductsRequest buyProductsRequest) {

    }
}
