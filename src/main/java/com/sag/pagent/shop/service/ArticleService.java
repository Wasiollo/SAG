package com.sag.pagent.shop.service;

import com.sag.pagent.shop.domain.Article;
import com.sag.pagent.shop.domain.ArticleType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ArticleService {

    public List<Article> generateStoreSupply(Integer maxGeneratedSupplies) {
        List<ArticleType> randomlyChosenArticleTypes = chooseArticleTypesRandomly();

        return randomlyChosenArticleTypes.stream()
                .map(articleType -> Article.builder()
                        .name(articleType.getName())
                        .price(articleType.generatePrice())
                        .amount(ThreadLocalRandom.current().nextInt(0, maxGeneratedSupplies + 1))
                        .build())
                .collect(Collectors.toList());
    }

    public List<Article> generateClientNeeds(Integer maxGeneratedNeeds) {
        List<ArticleType> randomlyChosenArticleTypes = chooseArticleTypesRandomly();

        return randomlyChosenArticleTypes.stream()
                .map(articleType -> Article.builder()
                        .name(articleType.getName())
                        .amount(ThreadLocalRandom.current().nextInt(0, maxGeneratedNeeds + 1))
                        .build())
                .collect(Collectors.toList());
    }

    private List<ArticleType> chooseArticleTypesRandomly(){
        List<ArticleType> articleTypes = new ArrayList<>(Arrays.asList(ArticleType.values()));
        return articleTypes.stream()
                .filter(articleType -> new Random().nextBoolean())
                .collect(Collectors.toList());
    }
}
