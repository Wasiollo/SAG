package com.sag.pagent.messages;

import com.sag.pagent.shop.domain.Article;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ArticlesStatusReply implements Serializable {
    private final List<Article> articlesToSell;
    private final String uid;

    public ArticlesStatusReply(List<Article> articlesToSell){
        this(articlesToSell, MessagesUtils.generateRandomStringByUUIDNoDash());
    }
}
