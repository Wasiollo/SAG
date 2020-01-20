package com.sag.pagent.behaviors;

import com.sag.pagent.shop.domain.Article;
import com.sag.pagent.shop.service.ArticleService;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class RegenerateShopSuppliesBehaviour extends MyTickerBehaviour {
    private SupplyGeneratedListener supplyGeneratedListener;
    private static final Integer MAX_GENERATED_SUPPLIES = 30;

    public interface SupplyGeneratedListener extends Serializable {
        void supplyGenerated(List<Article> articles);
    }

    public RegenerateShopSuppliesBehaviour(Agent a, long timeout, SupplyGeneratedListener supplyGeneratedListener) {
        super(a, timeout);
        this.supplyGeneratedListener = supplyGeneratedListener;
    }

    @Override
    protected void onTick() {
        List<Article> generatedArticles = ArticleService.generateStoreSupply(MAX_GENERATED_SUPPLIES);

        log.debug("Generated new products for shop {}", myAgent.getLocalName());
        supplyGeneratedListener.supplyGenerated(generatedArticles);
    }
}
