package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.manager.PurchaseOrderManager;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BrokerHierarchy implements Serializable {
    private PurchaseOrderManager purchaseOrderManager;
    private Map<BrokerHierarchyKey, Double> hierarchy = new HashMap<>();
    private Map<AID, Boolean> isBrokerAliveMap = new HashMap<>();
    private Map<BrokerHierarchyKey, Boolean> brokerBoughtAny = new HashMap<>();

    public BrokerHierarchy(PurchaseOrderManager purchaseOrderManager) {
        this.purchaseOrderManager = purchaseOrderManager;
    }

    public abstract void updateHierarchy(AID sender, BuyProductsResponse response, BuyProductsRequest buyProductsRequest);

    public Double getMultiplier(ArticleType type, AID broker) {
        return hierarchy.get(new BrokerHierarchyKey(broker, type));
    }

    public void updateBrokers(List<AID> brokers) {
        List<ArticleType> types = Arrays.asList(ArticleType.values());
        brokers.forEach(broker -> {
            isBrokerAliveMap.putIfAbsent(broker, true);
            types.forEach(type -> {
                hierarchy.putIfAbsent(new BrokerHierarchyKey(broker, type), 1d);
                brokerBoughtAny.putIfAbsent(new BrokerHierarchyKey(broker, type), true);
            });
        });
    }

    public List<AID> getAliveBrokerList() {
        return isBrokerAliveMap.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public PurchaseOrderManager getPurchaseOrderManager() {
        return purchaseOrderManager;
    }

    public boolean isFinished(ArticleType type) {
        return getAliveBrokerList().stream()
                .noneMatch(broker -> brokerBoughtAny.get(new BrokerHierarchyKey(broker, type)));
    }

    public void setBrokerBoughtAny(AID broker, ArticleType type, boolean boughtAny) {
        brokerBoughtAny.put(new BrokerHierarchyKey(broker, type), boughtAny);
    }
}
