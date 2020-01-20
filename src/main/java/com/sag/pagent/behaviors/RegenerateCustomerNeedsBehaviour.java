package com.sag.pagent.behaviors;

import com.sag.pagent.shop.domain.Article;
import com.sag.pagent.shop.service.ArticleService;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class RegenerateCustomerNeedsBehaviour extends MyTickerBehaviour {
    private NeedsGeneratedListener needsGeneratedListener;
    private static final Integer MAX_GENERATED_NEEDS = 10;

    public interface NeedsGeneratedListener extends Serializable {
        void needsGenerated(List<Article> articles);
    }

    public RegenerateCustomerNeedsBehaviour(Agent a, long timeout, NeedsGeneratedListener needsGeneratedListener) {
        super(a, timeout);
        this.needsGeneratedListener = needsGeneratedListener;
    }

    @Override
    protected void onTick() {
        List<Article> generatedArticles = ArticleService.generateClientNeeds(MAX_GENERATED_NEEDS);

        log.debug("Generated new needs for customer {}", myAgent.getLocalName());
        needsGeneratedListener.needsGenerated(generatedArticles);
    }
}
