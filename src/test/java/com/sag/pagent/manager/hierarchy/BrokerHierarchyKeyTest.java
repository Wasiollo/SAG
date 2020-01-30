package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BrokerHierarchyKeyTest {

    @Test
    void testHashCodeEqualAidEqualArticleType() {
        assertEquals(
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT).hashCode(),
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT).hashCode()
        );
    }

    @Test
    void testHashCodeNotEqualAidEqualArticleType() {
        assertNotEquals(
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT).hashCode(),
                new BrokerHierarchyKey(new AID("b", AID.ISGUID), ArticleType.BLUE_PAINT).hashCode()
        );
    }

    @Test
    void testHashCodeEqualAidNotEqualArticleType() {
        assertNotEquals(
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT).hashCode(),
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.COPPER_PIPE).hashCode()
        );
    }

    @Test
    void testHashCodeNotEqualAidNotEqualArticleType() {
        assertNotEquals(
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT).hashCode(),
                new BrokerHierarchyKey(new AID("b", AID.ISGUID), ArticleType.COPPER_PIPE).hashCode()
        );
    }

    @Test
    void testEqualsEqualAidEqualArticleType() {
        assertEquals(
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT),
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT)
        );
    }

    @Test
    void testEqualsNotEqualAidEqualArticleType() {
        assertNotEquals(
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT),
                new BrokerHierarchyKey(new AID("b", AID.ISGUID), ArticleType.BLUE_PAINT)
        );
    }

    @Test
    void testEqualsEqualAidNotEqualArticleType() {
        assertNotEquals(
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT),
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.COPPER_PIPE)
        );
    }

    @Test
    void testEqualsNotEqualAidNotEqualArticleType() {
        assertNotEquals(
                new BrokerHierarchyKey(new AID("a", AID.ISGUID), ArticleType.BLUE_PAINT),
                new BrokerHierarchyKey(new AID("b", AID.ISGUID), ArticleType.COPPER_PIPE)
        );
    }
}