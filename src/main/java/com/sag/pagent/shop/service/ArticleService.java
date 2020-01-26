package com.sag.pagent.shop.service;

import com.sag.pagent.shop.domain.Article;
import com.sag.pagent.shop.domain.ArticleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ArticleService {

    public static List<Article> generateStoreSupply(Integer maxGeneratedSupplies) {
        List<ArticleType> randomlyChosenArticleTypes = chooseArticleTypesRandomly();

        return randomlyChosenArticleTypes.stream()
                .map(articleType -> Article.builder()
                        .name(articleType.getName())
                        .price(articleType.generatePrice())
                        .amount(ThreadLocalRandom.current().nextInt(0, maxGeneratedSupplies + 1))
                        .build())
                .filter(article -> article.getAmount() > 0)
                .collect(Collectors.toList());
    }

    public static List<Article> generateClientNeeds(Integer maxGeneratedNeeds) {
        List<ArticleType> randomlyChosenArticleTypes = chooseArticleTypesRandomly();

        return randomlyChosenArticleTypes.stream()
                .map(articleType -> Article.builder()
                        .name(articleType.getName())
                        .amount(ThreadLocalRandom.current().nextInt(1, maxGeneratedNeeds + 1))
                        .build())
                .collect(Collectors.toList());
    }

    public static List<ArticleType> chooseArticleTypesRandomly() {
        List<ArticleType> articleTypes = new ArrayList<>(Arrays.asList(ArticleType.values()));
        return articleTypes.stream()
                .filter(articleType -> new Random().nextBoolean())
                .collect(Collectors.toList());
    }
}
