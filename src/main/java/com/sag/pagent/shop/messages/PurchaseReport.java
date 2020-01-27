package com.sag.pagent.shop.messages;

import com.sag.pagent.shop.articles.ArticleType;
import lombok.Data;

import java.io.Serializable;


@Data
public class PurchaseReport implements Serializable {
    private final ArticleType articleType;
    private final Integer amount;
    private final Double expense;
}
