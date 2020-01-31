package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.manager.PurchaseOrderManager;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;

import static java.lang.Math.max;

public class BrokerSamplingHierarchy extends BrokerHierarchy {
    int iteration;

    public BrokerSamplingHierarchy(PurchaseOrderManager purchaseOrderManager) {
        super(purchaseOrderManager);
    }

    @Override
    public int getNextAmount(AID broker, ArticleType type, int maxAmount) {
        iteration = max(iteration, getStartIteration());
        iteration++;

        return (int) getAverage(type) * iteration / getBrokerSize();
    }

    @Override
    public double getNextBudget(AID broker, ArticleType type) {
        iteration = max(iteration, getStartIteration());

        return iteration * getAverage(type);
    }

    private int getStartIteration() {
        return getBrokerSize() * 5;
    }
}
