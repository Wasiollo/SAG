package com.sag.pagent.shop.articles;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Article implements Serializable {
    private ArticleType articleType;
    private Double price;
    private Integer amount;

    public void addAmount(Integer amountToAdd){
        amount += amountToAdd;
    }

    public void minusAmount(Integer amountToAdd){
        amount -= amountToAdd;
    }
}
