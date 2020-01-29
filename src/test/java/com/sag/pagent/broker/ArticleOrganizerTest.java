package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleOrganizerTest {
    private ArticleOrganizer articleOrganizer;
    private AID aid1 = new AID("testAgent_1", AID.ISGUID);
    private AID aid2 = new AID("testAgent_2", AID.ISGUID);
    private AID aid3 = new AID("testAgent_3", AID.ISGUID);

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        articleOrganizer = new ArticleOrganizer();
    }

    @Test
    void setArticleList() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 4)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 4, 40d);
        assertEquals(1, shopArticleList.size());

        ShopArticle shopArticle = shopArticleList.iterator().next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(4, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());
    }

    @Test
    void getLowestPriceShopArticleListForZeroAmount() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 0, 10d);
        assertEquals(0, shopArticleList.size());
    }

    @Test
    void getLowestPriceShopArticleListForZeroBudget() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 1, 0);
        assertEquals(0, shopArticleList.size());
    }

    @Test
    void getLowestPriceShopArticleListForTooSmallBudget() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 1, 5d);
        assertEquals(0, shopArticleList.size());
    }

    @Test
    void getLowestPriceShopArticleListForLessAmountThanLowestElement() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 1, 10d);
        assertEquals(1, shopArticleList.size());

        ShopArticle shopArticle = shopArticleList.iterator().next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(1, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());

        shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 1, 10d);
        assertEquals(1, shopArticleList.size());

        shopArticle = shopArticleList.iterator().next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(1, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());

        shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 1, 20d);
        assertEquals(1, shopArticleList.size());

        shopArticle = shopArticleList.iterator().next();
        assertEquals(aid2, shopArticle.getShopAgent());
        assertEquals(1, shopArticle.getAmount());
        assertEquals(20d, shopArticle.getPrice());
    }

    @Test
    void getLowestPriceShopArticleListForLessBudgetThanLowestElement() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 2, 10d);
        assertEquals(1, shopArticleList.size());

        ShopArticle shopArticle = shopArticleList.iterator().next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(1, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());

        shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 2, 10d);
        assertEquals(1, shopArticleList.size());

        shopArticle = shopArticleList.iterator().next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(1, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());

        shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 2, 20d);
        assertEquals(1, shopArticleList.size());

        shopArticle = shopArticleList.iterator().next();
        assertEquals(aid2, shopArticle.getShopAgent());
        assertEquals(1, shopArticle.getAmount());
        assertEquals(20d, shopArticle.getPrice());
    }

    @Test
    void getLowestPriceShopArticleListForEqualAmountAndBudget() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 2, 20d);
        assertEquals(1, shopArticleList.size());

        ShopArticle shopArticle = shopArticleList.iterator().next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());

        shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 2, 40d);
        assertEquals(1, shopArticleList.size());

        shopArticle = shopArticleList.iterator().next();
        assertEquals(aid2, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(20d, shopArticle.getPrice());
    }

    @Test
    void getLowestPriceShopArticleListForEqualAmountAndBiggerBudget() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 2, 40d);
        assertEquals(1, shopArticleList.size());

        ShopArticle shopArticle = shopArticleList.iterator().next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());

        shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 2, 100d);
        assertEquals(1, shopArticleList.size());

        shopArticle = shopArticleList.iterator().next();
        assertEquals(aid2, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(20d, shopArticle.getPrice());
    }

    @Test
    void getLowestPriceShopArticleListForEqualAmountForTwoElementAndEqualBudget() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 4, 60d);
        assertEquals(2, shopArticleList.size());

        Iterator<ShopArticle> iterator = shopArticleList.iterator();
        ShopArticle shopArticle = iterator.next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());
        shopArticle = iterator.next();
        assertEquals(aid2, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(20d, shopArticle.getPrice());

        shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 2, 60d);
        assertEquals(1, shopArticleList.size());

        shopArticle = shopArticleList.iterator().next();
        assertEquals(aid3, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(30d, shopArticle.getPrice());
    }

    @Test
    void getLowestPriceShopArticleListForEqualAmountForTwoElementAndBiggerBudget() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 4, 100d);
        assertEquals(2, shopArticleList.size());

        Iterator<ShopArticle> iterator = shopArticleList.iterator();
        ShopArticle shopArticle = iterator.next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());
        shopArticle = iterator.next();
        assertEquals(aid2, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(20d, shopArticle.getPrice());
    }

    @Test
    void getLowestPriceShopArticleListForEqualAmountForTwoElementAndLowerBudget() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        articleOrganizer.setArticleList(aid2, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 20d, 2)));
        articleOrganizer.setArticleList(aid3, Arrays.asList(new Article(ArticleType.BLUE_PAINT, 30d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 4, 50d);
        assertEquals(2, shopArticleList.size());

        Iterator<ShopArticle> iterator = shopArticleList.iterator();
        ShopArticle shopArticle = iterator.next();
        assertEquals(aid1, shopArticle.getShopAgent());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());
        shopArticle = iterator.next();
        assertEquals(aid2, shopArticle.getShopAgent());
        assertEquals(1, shopArticle.getAmount());
        assertEquals(20d, shopArticle.getPrice());
    }

    @Test
    void getLowestPriceShopArticleListForDifferentTypes() {
        articleOrganizer.setArticleList(aid1, Arrays.asList(
                new Article(ArticleType.BLUE_PAINT, 10d, 2),
                new Article(ArticleType.COPPER_PIPE, 10d, 2)));

        List<ShopArticle> shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.BLUE_PAINT, 4, 100d);
        assertEquals(1, shopArticleList.size());

        ShopArticle shopArticle = shopArticleList.iterator().next();
        assertEquals(ArticleType.BLUE_PAINT, shopArticle.getArticleType());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());

        shopArticleList = articleOrganizer.getLowestPriceShopArticleList(ArticleType.COPPER_PIPE, 4, 100d);
        assertEquals(1, shopArticleList.size());

        shopArticle = shopArticleList.iterator().next();
        assertEquals(ArticleType.COPPER_PIPE, shopArticle.getArticleType());
        assertEquals(2, shopArticle.getAmount());
        assertEquals(10d, shopArticle.getPrice());
    }
}