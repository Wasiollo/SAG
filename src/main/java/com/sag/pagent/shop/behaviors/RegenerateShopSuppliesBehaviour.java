package com.sag.pagent.shop.behaviors;

import com.sag.pagent.behaviors.MyTickerBehaviour;
import com.sag.pagent.shop.articles.Article;
import com.sag.pagent.shop.articles.ArticleService;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

import static com.sag.pagent.Constant.MAX_GENERATED_SUPPLIES;

@Slf4j
public class RegenerateShopSuppliesBehaviour extends MyTickerBehaviour {
    private SupplyGeneratedListener supplyGeneratedListener;

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
