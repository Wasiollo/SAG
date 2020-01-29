package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

import static java.lang.Math.min;

@RequiredArgsConstructor
@Getter
@ToString
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
        return min((int) (budget / getPrice()), getAmount());
    }

    public void buy(int buy) {
        article.minusAmount(min(buy, getAmount()));
    }

    public boolean empty() {
        return getAmount() == 0;
    }
}
