package com.sag.pagent.shop.service;

import com.sag.pagent.shop.domain.Article;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ArticleService {

    public List<Article> generateStoreSupply(Integer maxGeneratedSupplies) {
        List<Article> generatedSupplies = new ArrayList<>(Arrays.asList(Article.values()));
        List<Article> randomlyChosenArticlesWithPrices = generatedSupplies.stream()
                .filter(article -> new Random().nextBoolean())
                .peek(Article::generatePrice)
                .collect(Collectors.toList());

        return randomlyChosenArticlesWithPrices.stream()
                .map(article -> Collections.nCopies(maxGeneratedSupplies, article))
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(ArrayList::new),
                        list -> {
                            Collections.shuffle(list);
                            return list.stream();
                        }))
                .limit(ThreadLocalRandom.current().nextInt(0, maxGeneratedSupplies + 1))
                .collect(Collectors.toList());

    }

}
