package com.sag.pagent.manager.messages;

import com.sag.pagent.shop.articles.ArticleType;
import lombok.Data;

@Data
public class BuyProductsResponse {
    private final ArticleType articleType;
    private final Integer amount;
    private final Double usedMoney;
}
