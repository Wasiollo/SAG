package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class ArticleOrganizer {
    private final ArticleType articleType;
    private Set<ShopArticle> articleSet = new TreeSet<>(comparatorArticle);
    private Map<AID, ShopArticle> articleMap = new HashMap<>();

    private static final Comparator<ShopArticle> comparatorArticle = Comparator
            .comparing(ShopArticle::getPrice)
            .thenComparing(ShopArticle::getShopAgent);

}
