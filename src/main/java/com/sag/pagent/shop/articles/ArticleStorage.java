package com.sag.pagent.shop.articles;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class ArticleStorage {
    private List<Article> articles = new ArrayList<>();

    public void extend(List<Article> supplies) {
        for (Article article : supplies) {
            Optional<Article> shopArticle = articles.stream()
                    .filter(art -> art.getName().equals(article.getName()))
                    .findAny();
            if (shopArticle.isPresent()) {
                shopArticle.get().addAmount(article.getAmount());
                shopArticle.get().setPrice(article.getPrice());
            } else {
                articles.add(article);
            }
        }
    }

    @SuppressWarnings("unused")
    private List<Article> getArticle(List<Article> articlesToBuy) {
        List<Article> articlesToStatusReply = new ArrayList<>();
        articlesToBuy.forEach(atb ->
                articles.stream()
                        .filter(sa -> atb.getName().equals(sa.getName()))
                        .findAny()
                        .ifPresent(sa -> {
                            if (sa.getAmount() < atb.getAmount()) {
                                articlesToStatusReply.add(sa);
                            } else {
                                Article articleOffer = Article.builder()
                                        .name(sa.getName())
                                        .price(sa.getPrice())
                                        .amount(atb.getAmount())
                                        .build();
                                articlesToStatusReply.add(articleOffer);
                            }
                        }));
        return articlesToStatusReply;
    }
}
