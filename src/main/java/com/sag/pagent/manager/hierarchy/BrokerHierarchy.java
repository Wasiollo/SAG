package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.manager.messages.BuyProductsResponse;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BrokerHierarchy {
    private Map<BrokerHierarchyKey, Double> hierarchy = new HashMap<>();

    public abstract void updateHierarchy(AID sender, BuyProductsResponse response);

    public Double getMultiplier(ArticleType type, AID broker) {
        return hierarchy.get(new BrokerHierarchyKey(broker, type));
    }

    public void initializeHierarchy(List<AID> brokers) {
        List<ArticleType> types = Arrays.asList(ArticleType.values());
        types.forEach(type -> brokers.forEach(broker -> hierarchy.put(new BrokerHierarchyKey(broker, type), 1d)));

    }
}
