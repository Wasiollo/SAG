package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ShopArticle {
    private final Article article;
    private final AID shopAgent;

    public ArticleType getArticleType() {
        return article.getArticleType();
    }

    public Double getPrice() {
        return article.getPrice();
    }
}
