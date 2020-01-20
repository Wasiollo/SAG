package com.sag.pagent.shop.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Article {
    private String name;
    private Double price;
    private Integer amount;
}
