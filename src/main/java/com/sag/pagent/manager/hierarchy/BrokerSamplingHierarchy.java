package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.manager.PurchaseOrderManager;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;

import static java.lang.Math.max;

public class BrokerSamplingHierarchy extends BrokerHierarchy {
    int iteration;

    public BrokerSamplingHierarchy(PurchaseOrderManager purchaseOrderManager) {
        super(purchaseOrderManager);
    }

    @Override
    public void update(AID broker, BuyProductsRequest request, BuyProductsResponse response) {
        if (response.getBoughtAmount() == 0) return;
        ArticleType articleType = request.getArticleType();

        double boughtRate = response.getUsedMoney() / response.getBoughtAmount();
        double lastBoughtRate = getMultiplier(broker, articleType);
        setMultiplier(broker, articleType, lastBoughtRate * 0.25d + boughtRate * 0.75d);
    }

    @Override
    public int getNextAmount(AID broker, ArticleType type) {
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
