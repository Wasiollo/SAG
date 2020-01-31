package com.sag.pagent.manager;

import com.sag.pagent.customer.order.OrderArticle;
import com.sag.pagent.manager.messages.BuyProductsResponse;
import com.sag.pagent.manager.messages.PurchaseOrder;
import com.sag.pagent.shop.articles.ArticleType;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.StrictMath.min;

@Slf4j
public class PurchaseOrderManager implements Serializable {
    private Map<String, Map<ArticleType, Integer>> articlesToBuyMap = new HashMap<>();
    private Map<String, Double> budgetMap = new HashMap<>();

    public void addPurchaseOrder(PurchaseOrder purchaseOrder) {
        String customerAgentId = purchaseOrder.getCustomerAgentId();
        articlesToBuyMap.putIfAbsent(customerAgentId, new HashMap<>());
        budgetMap.putIfAbsent(customerAgentId, 0d);
        addPurchaseOrder(customerAgentId, purchaseOrder.getArticlesToBuy(), purchaseOrder.getBudget());
    }

    private void addPurchaseOrder(String customerAgentId, List<OrderArticle> articles, double budget) {
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

    public List<ArticleType> getArticleTypeList(String customerAgentId) {
        return new ArrayList<>(articlesToBuyMap.get(customerAgentId).keySet());
    }

    public int getMinAmount(String customerAgentId, ArticleType articleType, int amount) {
        int currAmount = articlesToBuyMap.get(customerAgentId).get(articleType);
        int minusAmount = min(amount, currAmount);
        articlesToBuyMap.get(customerAgentId).put(articleType, currAmount - minusAmount);
        return minusAmount;
    }

    public double getMinBudget(String customerAgentId, double budget) {
        double currBudget = budgetMap.get(customerAgentId);
        double minusBudget = min(budget, currBudget);
        budgetMap.put(customerAgentId, currBudget - minusBudget);
        return minusBudget;
    }

    public void addAmount(String customerAgentId, ArticleType articleType, Integer amount) {
        int currAmount = articlesToBuyMap.get(customerAgentId).get(articleType);
        articlesToBuyMap.get(customerAgentId).put(articleType, currAmount + amount);
    }

    public void recover(String customerAgentId, BuyProductsResponse response) {
        ArticleType articleType = response.getRequest().getArticleType();
        int remainAmount = response.getRequest().getAmount() - response.getBoughtAmount();
        double remainBudget = response.getRequest().getBudget() - response.getUsedMoney();

        int currAmount = articlesToBuyMap.get(customerAgentId).get(articleType);
        articlesToBuyMap.get(customerAgentId).put(articleType, currAmount + remainAmount);

        double currBudget = budgetMap.get(customerAgentId);
        budgetMap.put(customerAgentId, currBudget + remainBudget);
    }

    public int getAmount(String customerAgentId, ArticleType articleType) {
        Map<ArticleType, Integer> articlesToBuy = articlesToBuyMap.get(customerAgentId);
        if (articlesToBuy == null) return -1;
        Integer remaining = articlesToBuy.get(articleType);
        if (remaining == null) return -2;
        return remaining;
    }
}
