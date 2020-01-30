package com.sag.pagent.broker;

import com.sag.pagent.broker.messages.BuyProductsRequest;
import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

@Slf4j
public class ArticleOrganizer implements Serializable {
    private Map<ArticleType, Queue<ShopArticle>> shopArticleQueueMap = new EnumMap<>(ArticleType.class);
    private Map<ArticleType, Map<AID, ShopArticle>> shopArticleMapMap = new EnumMap<>(ArticleType.class);

    public void setArticleList(AID shopAgent, List<Article> articleList) {
        for (Article article : articleList) {
            shopArticleQueueMap.putIfAbsent(article.getArticleType(), new PriorityQueue<>());
            shopArticleMapMap.putIfAbsent(article.getArticleType(), new HashMap<>());
            setArticle(shopAgent, article);
        }
    }

    private void setArticle(AID shopAgent, Article article) {
        Queue<ShopArticle> shopArticleQueue = shopArticleQueueMap.get(article.getArticleType());
        Map<AID, ShopArticle> shopArticleMap = shopArticleMapMap.get(article.getArticleType());

        ShopArticle shopArticle = shopArticleMap.get(shopAgent);
        if (shopArticle != null) {
            shopArticle.getArticle().setAmount(article.getAmount());
            return;
        }
        shopArticle = new ShopArticle(shopAgent, article);
        shopArticleMap.put(shopAgent, shopArticle);
        shopArticleQueue.add(shopArticle);
    }

    public List<ShopArticle> getLowestPriceShopArticleList(BuyProductsRequest buyProductsRequest) {
        return getLowestPriceShopArticleList(
                buyProductsRequest.getArticleType(),
                buyProductsRequest.getAmount(),
                buyProductsRequest.getBudget()
        );
    }

    public List<ShopArticle> getLowestPriceShopArticleList(ArticleType articleType, int amount, double budget) {
        Queue<ShopArticle> shopArticleQueue = shopArticleQueueMap.get(articleType);
        Map<AID, ShopArticle> shopArticleMap = shopArticleMapMap.get(articleType);
        List<ShopArticle> shopArticleList = new LinkedList<>();
        if (shopArticleQueue == null) {
            log.debug("ArticleType {} not found in shop", articleType);
            return shopArticleList;
        }

        while (!shopArticleQueue.isEmpty() && amount > 0 && budget > 0) {
            ShopArticle shopArticle = shopArticleQueue.peek();
            int bought = shopArticle.getArticle().buy(amount, budget);
            if (bought == 0) break;
            amount -= bought;
            budget -= bought * shopArticle.getPrice();
            shopArticleList.add(new ShopArticle(shopArticle, bought));
            if (shopArticle.getAmount() == 0) {
                shopArticleMap.remove(shopArticle.getShopAgent());
                shopArticleQueue.remove();
            }
        }
        return shopArticleList;
    }
}
