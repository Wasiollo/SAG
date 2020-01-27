package com.sag.pagent.broker.messages;

import com.sag.pagent.shop.articles.ArticleType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class BuyProducts implements Serializable {
    private final ArticleType article;
    private final Integer amount;
    private final Double budget;
}
