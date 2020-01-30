package com.sag.pagent.manager;

import com.sag.pagent.customer.order.OrderArticle;
import com.sag.pagent.manager.messages.PurchaseOrder;
import com.sag.pagent.shop.articles.ArticleType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PurchaseOrderManager {
    private Map<String, Map<ArticleType, Integer>> articlesToBuyMap = new HashMap<>();
    private Map<String, Double> budgetMap = new HashMap<>();

    public void updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        String customerAgentId = purchaseOrder.getCustomerAgentId();
        articlesToBuyMap.putIfAbsent(customerAgentId, new HashMap<>());
        budgetMap.putIfAbsent(customerAgentId, 0d);
        updatePurchaseOrder(customerAgentId, purchaseOrder.getArticlesToBuy(), purchaseOrder.getBudget());
    }

    private void updatePurchaseOrder(String customerAgentId, List<OrderArticle> articles, double budget) {
        Double savedBudget = budgetMap.get(customerAgentId);
        addArticlesToBuy(customerAgentId, articles);
        budgetMap.put(customerAgentId, savedBudget + budget);
        log.debug("Budget: " + budget);
    }

    private void addArticlesToBuy(String customerAgentId, List<OrderArticle> articles) {
        Map<ArticleType, Integer> articlesToBuy = articlesToBuyMap.get(customerAgentId);

        articles.forEach(article -> {
            if (articlesToBuy.containsKey(article.getArticle())) {
                Integer articleAmount = articlesToBuy.get(article.getArticle()) + article.getAmount();
                articlesToBuy.put(article.getArticle(), articleAmount);
                log.debug("Article {} existed, amount set to : {}", article.getArticle(), articleAmount);
            } else {
                articlesToBuy.put(article.getArticle(), article.getAmount());
                log.debug("Article {} didn't exist, amount set to: {}", article.getArticle(), article.getAmount());
            }
        });
    }
}
