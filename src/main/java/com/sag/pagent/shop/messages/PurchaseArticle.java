package com.sag.pagent.shop.messages;

import com.sag.pagent.shop.articles.ArticleType;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
@Getter
public class PurchaseArticle implements Serializable {
    private final ArticleType articleType;
    private final Integer amount;
    private final Double budget;
}
