package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.manager.PurchaseOrderManager;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;

public class BrokerBudgetQuantizationHierarchy extends BrokerHierarchy {
    public BrokerBudgetQuantizationHierarchy(PurchaseOrderManager purchaseOrderManager) {
        super(purchaseOrderManager);
    }

    @Override
    public void update(AID broker, BuyProductsRequest request, BuyProductsResponse response) {

    }

    @Override
    public int getNextAmount(AID broker, ArticleType typ) {
        return 0;
    }

    @Override
    public double getNextBudget(AID broker, ArticleType typ) {
        return 0;
    }
}
