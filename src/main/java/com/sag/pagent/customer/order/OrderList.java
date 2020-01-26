package com.sag.pagent.customer.order;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Data
@Builder
public class OrderList implements Serializable {
    @NonNull
    private List<OrderArticle> orderArticles;
    private double remainingBudget;

    public OrderList(@NonNull List<OrderArticle> orderArticles, double remainingBudget) {
        this.orderArticles = orderArticles;
        Collections.sort(this.orderArticles);
        this.remainingBudget = remainingBudget;
    }

    public List<OrderList> splitOrder(int amount) {
        log.debug("Split OrderList to {} parts ", amount);
        Map<Integer, List<OrderArticle>> orderArticleSplitMap = getSplitOrderArticleMap(amount);

        List<OrderList> splitOrderList = new LinkedList<>();

        log.debug("Before split {}", toString());
        for (Map.Entry<Integer, List<OrderArticle>> entry : orderArticleSplitMap.entrySet()) {
            List<OrderArticle> newOrderArticles = mergeOrderArticle(entry.getValue());
            OrderList orderList = new OrderList(newOrderArticles, remainingBudget / amount);
            splitOrderList.add(orderList);
            log.debug("After split {}", orderList.toString());
        }

        return splitOrderList;
    }

    private Map<Integer, List<OrderArticle>> getSplitOrderArticleMap(int amount) {
        return orderArticles.stream()
                .map(article -> Collections.nCopies(article.getAmount(), OrderArticle.builder()
                        .article(article.getArticle())
                        .amount(1)
                        .build()))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(x -> ThreadLocalRandom.current().nextInt(amount)));
    }

    @NotNull
    private List<OrderArticle> mergeOrderArticle(List<OrderArticle> orderArticleList) {
        return orderArticleList.stream()
                .collect(Collectors.groupingBy(OrderArticle::getArticle))
                .entrySet()
                .stream()
                .map(e -> OrderArticle.builder()
                        .article(e.getKey())
                        .amount(e.getValue().size())
                        .build())
                .collect(Collectors.toList());
    }
}
