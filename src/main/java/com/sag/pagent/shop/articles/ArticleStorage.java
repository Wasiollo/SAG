package com.sag.pagent.shop.articles;

import com.sag.pagent.shop.messages.PurchaseArticle;
import com.sag.pagent.shop.messages.PurchaseReport;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;


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
        return (List<Article>) articleMap.values();
    }

    public PurchaseReport purchase(PurchaseArticle purchaseArticle) {
        Article article = articleMap.get(purchaseArticle.getArticleType());
        if (article == null) {
            return new PurchaseReport(purchaseArticle.getArticleType(), 0, 0d);
        }
        int bought = min(article.getAmount(), purchaseArticle.getAmount());
        double price = article.getPrice() * bought;
        article.minusAmount(bought);

        return new PurchaseReport(article.getArticleType(), bought, price);
    }
}
