package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.shop.articles.ArticleType;

import java.util.List;

public interface BrokerHierarchy {
    List getHierarchy(ArticleType type);
}
