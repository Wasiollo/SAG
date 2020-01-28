package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;

import java.util.*;

public class ArticleOrganizer {
    private Map<ArticleType, Queue<ShopArticle>> shopArticleQueueMap = new EnumMap<>(ArticleType.class);
    private Map<ArticleType, Map<AID, ShopArticle>> shopArticleMapMap = new EnumMap<>(ArticleType.class);

    private static final Comparator<ShopArticle> comparatorArticle = Comparator
            .comparing(ShopArticle::getPrice)
            .thenComparing(ShopArticle::getShopAgent);

    public void setArticleList(AID shopAgent, List<Article> articleList) {
        for (Article article : articleList) {
            shopArticleQueueMap.computeIfAbsent(article.getArticleType(), k -> new PriorityQueue<>(comparatorArticle));
            shopArticleMapMap.computeIfAbsent(article.getArticleType(), k -> new HashMap<>());
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

    public List<ShopArticle> getLowestPriceShopArticleList(ArticleType articleType, int amount, double budget) {
        Queue<ShopArticle> shopArticleQueue = shopArticleQueueMap.get(articleType);
        Map<AID, ShopArticle> shopArticleMap = shopArticleMapMap.get(articleType);

        List<ShopArticle> shopArticleList = new LinkedList<>();
        while (!shopArticleQueue.isEmpty() && amount > 0 && budget > 0) {
            ShopArticle shopArticle = shopArticleQueue.peek();
            int canBuy = shopArticle.howMachCanBuy(budget);
            if (canBuy == 0) break;
            amount -= canBuy;
            budget -= shopArticle.buy(budget);
            shopArticleList.add(new ShopArticle(shopArticle, canBuy));
            if (shopArticle.empty()) {
                shopArticleMap.remove(shopArticle.getShopAgent());
                shopArticleQueue.remove();
            }
        }
        return shopArticleList;
    }
}
