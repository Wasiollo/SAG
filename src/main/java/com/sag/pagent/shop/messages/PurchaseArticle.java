package com.sag.pagent.shop.messages;

import com.sag.pagent.broker.ShopArticle;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
@Getter
public class PurchaseArticle implements Serializable {
    private final AID shopAgent;
    private final ArticleType articleType;
    private final Integer amount;
    private final Double budget;

    public PurchaseArticle(ShopArticle shopArticle) {
        this.shopAgent = shopArticle.getShopAgent();
        this.articleType = shopArticle.getArticleType();
        this.amount = shopArticle.getAmount();
        this.budget = this.amount * shopArticle.getPrice();
    }
}
