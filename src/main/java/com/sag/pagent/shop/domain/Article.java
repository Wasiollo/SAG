package com.sag.pagent.shop.domain;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Article implements Serializable {
    private String name;
    private Double price;
    private Integer amount;

    public void addAmount(Integer amountToAdd){
        amount += amountToAdd;
    }
}
