package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopArticleTest {
    private final AID agent = new AID("testAgent", AID.ISGUID);
    private ShopArticle shopArticle;

    @BeforeEach
    void setUp() {
        Article article = new Article(ArticleType.BLUE_PAINT, 10d, 2);
        shopArticle = new ShopArticle(agent, article);
    }

    @Test
    void getArticleType() {
        assertEquals(ArticleType.BLUE_PAINT, shopArticle.getArticleType());
    }

    @Test
    void getPrice() {
        assertEquals(10d, shopArticle.getPrice());
    }

    @Test
    void getAmount() {
        assertEquals(2, shopArticle.getAmount());
    }

    @Test
    void howMachCanBuy() {
        assertEquals(0, shopArticle.howMachCanBuy(5));
        assertEquals(1, shopArticle.howMachCanBuy(10));
        assertEquals(1, shopArticle.howMachCanBuy(15));
        assertEquals(2, shopArticle.howMachCanBuy(20));
        assertEquals(2, shopArticle.howMachCanBuy(25));
        assertEquals(2, shopArticle.howMachCanBuy(30));
    }

    @Test
    void buyOne() {
        shopArticle.buy(1);
        assertEquals(1, shopArticle.getAmount());
    }

    @Test
    void buyEqual() {
        shopArticle.buy(2);
        assertEquals(0, shopArticle.getAmount());
    }

    @Test
    void buyTooMany() {
        shopArticle.buy(3);
        assertEquals(0, shopArticle.getAmount());
    }

    @Test
    void empty() {
        shopArticle.buy(2);
        assertTrue(shopArticle.empty());
    }
}