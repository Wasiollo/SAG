package com.sag.pagent.messages;

import com.sag.pagent.shop.domain.Article;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static com.sag.pagent.messages.MessagesUtils.generateRandomStringByUUIDNoDash;

@RequiredArgsConstructor
@Getter
public class ArticlesStatusQuery implements Serializable {
    private final List<Article> articlesToBuy;
    private final String uid;
    private final String purchaseUid;

    public ArticlesStatusQuery(List<Article> articles, String purchaseUid) {
        this(articles, generateRandomStringByUUIDNoDash(), purchaseUid);
    }
}
