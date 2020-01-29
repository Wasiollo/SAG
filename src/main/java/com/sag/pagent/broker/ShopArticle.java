package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class ShopArticle implements Serializable {
    private final AID shopAgent;
    private final Article article;

    public ShopArticle(ShopArticle shopArticle, int amount) {
        this(
                shopArticle.getShopAgent(),
                new Article(shopArticle.getArticle(), amount)
        );
    }

    public ArticleType getArticleType() {
        return article.getArticleType();
    }

    public Double getPrice() {
        return article.getPrice();
    }

    public Integer getAmount() {
        return article.getAmount();
    }

    public int howMachCanBuy(double budget) {
        return (int) (budget / getPrice());
    }

    public double buy(double budget) {
        int canBuy = howMachCanBuy(budget);
        return budget - (getPrice() * canBuy);
    }

    public boolean empty() {
        return getAmount() == 0;
    }
}
