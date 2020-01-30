package com.sag.pagent.customer.order;

import com.sag.pagent.shop.articles.ArticleService;
import com.sag.pagent.shop.articles.ArticleType;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
public class OrderGenerator {
    final int maxNeeds;
    final double minBudget;
    final double maxBudget;

    public OrderList generate() {
        List<OrderArticle> orderArticles = generateOrderArticle();
        double budget = generateBudget();
        return new OrderList(orderArticles, budget);
    }

    private List<OrderArticle> generateOrderArticle() {
        List<ArticleType> randomlyChosenArticleTypes = Collections.singletonList(
                ArticleService.chooseArticleTypesRandomly().iterator().next());

        return randomlyChosenArticleTypes.stream()
                .map(articleType -> OrderArticle.builder()
                        .article(articleType)
                        .amount(ThreadLocalRandom.current().nextInt(1, maxNeeds + 1))
                        .build())
                .collect(Collectors.toList());
    }

    private double generateBudget() {
        double budget = ThreadLocalRandom.current().nextDouble(minBudget, maxBudget + 1);
        return ((double) ((int) (budget * 100.0))) / 100.0;
    }
}
