package com.sag.pagent.shop.messages;

import com.sag.pagent.shop.articles.Article;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ArticlesStatusReply implements Serializable {
    private final List<Article> articlesToSell;
}
