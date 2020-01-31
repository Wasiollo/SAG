package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.manager.PurchaseOrderManager;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;

import static java.lang.Math.max;

public class BrokerBudgetQuantizationHierarchy extends BrokerHierarchy {
    int iteration;

    public BrokerBudgetQuantizationHierarchy(PurchaseOrderManager purchaseOrderManager) {
        super(purchaseOrderManager);
    }

    @Override
    public int getNextAmount(AID broker, ArticleType type, int maxAmount) {
        iteration = max(iteration, getStartIteration());
        iteration++;

        return maxAmount / getAliveBrokerList().size();
    }

    @Override
    public double getNextBudget(AID broker, ArticleType type) {
        return getAverage(type) * (iteration / getBrokerSize());
    }

    private int getStartIteration() {
        return getBrokerSize() * 10;
    }
}
