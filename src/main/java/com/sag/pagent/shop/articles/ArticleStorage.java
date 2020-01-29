package com.sag.pagent.shop.articles;

import com.sag.pagent.shop.messages.PurchaseArticle;
import com.sag.pagent.shop.messages.PurchaseReport;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class ArticleStorage {
    private Map<ArticleType, Article> articleMap = new EnumMap<>(ArticleType.class);

    public void extend(List<Article> supplies) {
        for (Article supply : supplies) {
            Article article = articleMap.get(supply.getArticleType());
            if (article == null) {
                articleMap.put(supply.getArticleType(), supply);
            } else {
                article.addAmount(supply.getAmount());
            }
        }
    }

    public List<Article> getArticles() {
        return new ArrayList<>(articleMap.values());
    }

    public PurchaseReport purchase(PurchaseArticle purchaseArticle) {
        return purchase(
                purchaseArticle.getArticleType(),
                purchaseArticle.getAmount(),
                purchaseArticle.getBudget()
        );
    }

    public PurchaseReport purchase(ArticleType articleType, int amount, double budget) {
        Article article = articleMap.get(articleType);
        if (article == null) {
            return new PurchaseReport(articleType, 0, 0d);
        }

        int bought = article.buy(amount, budget);
        double price = bought * article.getPrice();

        if (article.getAmount() == 0) {
            articleMap.remove(article.getArticleType());
        }

        return new PurchaseReport(article.getArticleType(), bought, price);
    }
}
