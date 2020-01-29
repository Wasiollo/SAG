package com.sag.pagent.shop.articles;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Comparator;

import static java.lang.Math.min;

@Data
public class Article implements Serializable, Comparable<Article> {
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

    public static final Comparator<Article> COMPARATOR = Comparator
            .comparing(Article::getArticleType)
            .thenComparingDouble(Article::getPrice)
            .thenComparingInt(Article::getAmount);

    @Override
    public int compareTo(@NotNull Article o) {
        return COMPARATOR.compare(this, o);
    }

    public int howMachCanBeBought(double budget) {
        return min((int) (budget / getPrice()), getAmount());
    }

    public int buy(int amount, double budget) {
        int canBeBought = howMachCanBeBought(budget);
        int toBuy = min(amount, canBeBought);
        minusAmount(toBuy);
        return toBuy;
    }
}
