package com.sag.pagent.shop.articles;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class Article implements Serializable {
    private ArticleType articleType;
    private Double price;
    private Integer amount;

    @Builder
    public Article(ArticleType articleType, Double price, Integer amount) {
        this.articleType = articleType;
        this.price = price;
        this.amount = amount;
    }

    public Article(Article article, int amount) {
        this.articleType = article.getArticleType();
        this.price = article.price;
        this.amount = amount;
    }

    public void addAmount(Integer amountToAdd) {
        amount += amountToAdd;
    }

    public void minusAmount(Integer amountToAdd) {
        amount -= amountToAdd;
    }
}
