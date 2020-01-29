package com.sag.pagent.broker;

import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}