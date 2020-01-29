package com.sag.pagent.shop.articles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleTest {
    private Article article;

    @BeforeEach
    void setUp() {
        article = new Article(ArticleType.BLUE_PAINT, 10d, 2);
    }

    @Test
    void howMachCanBeBought() {
        assertEquals(0, article.howMachCanBeBought(5));
        assertEquals(1, article.howMachCanBeBought(10));
        assertEquals(1, article.howMachCanBeBought(15));
        assertEquals(2, article.howMachCanBeBought(20));
        assertEquals(2, article.howMachCanBeBought(25));
        assertEquals(2, article.howMachCanBeBought(30));
    }

    @Test
    void buyNoAmountEqualBudget() {
        int bought = article.buy(0, 10);

        assertEquals(0, bought);
        assertEquals(2, article.getAmount());
    }

    @Test
    void buyEqualAmountLessBudget() {
        int bought = article.buy(0, 5);

        assertEquals(0, bought);
        assertEquals(2, article.getAmount());
    }

    @Test
    void buyOneEqualAmountEqualBudget() {
        int bought = article.buy(1, 10);

        assertEquals(1, bought);
        assertEquals(1, article.getAmount());
    }

    @Test
    void buyAllEqualAmountEqualBudget() {
        int bought = article.buy(2, 20);

        assertEquals(2, bought);
        assertEquals(0, article.getAmount());
    }

    @Test
    void buyEqualAmountMoreBudget() {
        int bought = article.buy(1, 30);

        assertEquals(1, bought);
        assertEquals(1, article.getAmount());
    }

    @Test
    void buyMoreAmountEqualBudget() {
        int bought = article.buy(2, 10);

        assertEquals(1, bought);
        assertEquals(1, article.getAmount());
    }
}