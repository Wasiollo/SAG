package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Comparator;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class ShopArticle implements Serializable, Comparable<ShopArticle> {
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

    public static final Comparator<ShopArticle> COMPARATOR = Comparator
            .comparing(ShopArticle::getArticle)
            .thenComparing(o -> o.getShopAgent().getName());

    @Override
    public int compareTo(@NotNull ShopArticle o) {
        return COMPARATOR.compare(this, o);
    }
}
