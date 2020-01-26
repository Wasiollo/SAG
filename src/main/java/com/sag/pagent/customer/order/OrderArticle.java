package com.sag.pagent.customer.order;

import com.sag.pagent.shop.articles.ArticleType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@Builder
public class OrderArticle implements Serializable, Comparable<OrderArticle> {
    @NonNull
    private ArticleType article;
    @NonNull
    private Integer amount;

    @Override
    public int compareTo(@NotNull OrderArticle o) {
        int articleComp = article.compareTo(o.article);
        if (articleComp != 0) return articleComp;
        return amount.compareTo(o.amount);
    }
}
