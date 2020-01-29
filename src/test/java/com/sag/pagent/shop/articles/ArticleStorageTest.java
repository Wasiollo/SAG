package com.sag.pagent.shop.articles;

import com.sag.pagent.shop.messages.PurchaseReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleStorageTest {
    private ArticleStorage articleStorage;

    @BeforeEach
    void setUp() {
        articleStorage = new ArticleStorage();
    }

    @Test
    void extend() {
        articleStorage.extend(Arrays.asList(
                new Article(ArticleType.BLUE_PAINT, 10d, 2),
                new Article(ArticleType.COPPER_PIPE, 10d, 2)));

        assertThat(articleStorage.getArticles(), hasItems(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        assertThat(articleStorage.getArticles(), hasItems(new Article(ArticleType.COPPER_PIPE, 10d, 2)));
    }

    @Test
    void extendOldType() {
        articleStorage.extend(Arrays.asList(
                new Article(ArticleType.BLUE_PAINT, 10d, 2),
                new Article(ArticleType.COPPER_PIPE, 10d, 2)));

        articleStorage.extend(Arrays.asList(
                new Article(ArticleType.BLUE_PAINT, 10d, 2),
                new Article(ArticleType.COPPER_PIPE, 10d, 2)));

        assertThat(articleStorage.getArticles(), hasItems(new Article(ArticleType.BLUE_PAINT, 10d, 4)));
        assertThat(articleStorage.getArticles(), hasItems(new Article(ArticleType.COPPER_PIPE, 10d, 4)));
    }

    @Test
    void extendNewType() {
        articleStorage.extend(Arrays.asList(
                new Article(ArticleType.BLUE_PAINT, 10d, 2),
                new Article(ArticleType.COPPER_PIPE, 10d, 2)));

        articleStorage.extend(Arrays.asList(
                new Article(ArticleType.GREEN_PAIN, 10d, 2),
                new Article(ArticleType.RAW_WOOD, 10d, 2)));

        assertThat(articleStorage.getArticles(), hasItems(new Article(ArticleType.BLUE_PAINT, 10d, 2)));
        assertThat(articleStorage.getArticles(), hasItems(new Article(ArticleType.COPPER_PIPE, 10d, 2)));
        assertThat(articleStorage.getArticles(), hasItems(new Article(ArticleType.GREEN_PAIN, 10d, 2)));
        assertThat(articleStorage.getArticles(), hasItems(new Article(ArticleType.RAW_WOOD, 10d, 2)));
    }

    @Test
    void purchaseOne() {
        articleStorage.extend(Arrays.asList(
                new Article(ArticleType.BLUE_PAINT, 10d, 2),
                new Article(ArticleType.COPPER_PIPE, 10d, 2)));

        PurchaseReport purchaseReport1 = articleStorage.purchase(ArticleType.BLUE_PAINT, 1, 10d);
        PurchaseReport purchaseReport2 = articleStorage.purchase(ArticleType.COPPER_PIPE, 1, 10d);

        assertEquals(1, purchaseReport1.getAmount());
        assertEquals(10d, purchaseReport1.getExpense());
        assertEquals(1, purchaseReport2.getAmount());
        assertEquals(10d, purchaseReport2.getExpense());
    }

    @Test
    void purchaseAll() {
        articleStorage.extend(Arrays.asList(
                new Article(ArticleType.BLUE_PAINT, 10d, 2),
                new Article(ArticleType.COPPER_PIPE, 10d, 2)));

        PurchaseReport purchaseReport1 = articleStorage.purchase(ArticleType.BLUE_PAINT, 2, 20d);
        PurchaseReport purchaseReport2 = articleStorage.purchase(ArticleType.COPPER_PIPE, 2, 20d);

        assertEquals(0, articleStorage.getArticles().size());
        assertEquals(2, purchaseReport1.getAmount());
        assertEquals(20d, purchaseReport1.getExpense());
        assertEquals(2, purchaseReport2.getAmount());
        assertEquals(20d, purchaseReport2.getExpense());
    }

    @Test
    void purchaseOneByOne() {
        articleStorage.extend(Arrays.asList(
                new Article(ArticleType.BLUE_PAINT, 10d, 2),
                new Article(ArticleType.COPPER_PIPE, 10d, 2)));

        articleStorage.purchase(ArticleType.BLUE_PAINT, 1, 10d);
        PurchaseReport purchaseReport = articleStorage.purchase(ArticleType.BLUE_PAINT, 1, 10d);

        assertEquals(1, purchaseReport.getAmount());
        assertEquals(10d, purchaseReport.getExpense());

        articleStorage.purchase(ArticleType.COPPER_PIPE, 1, 10d);
        purchaseReport = articleStorage.purchase(ArticleType.COPPER_PIPE, 1, 10d);

        assertEquals(1, purchaseReport.getAmount());
        assertEquals(10d, purchaseReport.getExpense());

        assertEquals(0, articleStorage.getArticles().size());
    }
}