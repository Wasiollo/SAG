package com.sag.pagent.shop.articles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Getter
public enum ArticleType implements Serializable {
    PVC_GUTTER("Rynna PCV", 1d, 10d),
    STEEL_GUTTER("Rynna Stalowa", 1d, 10d),
    WOOL_INSULATION("Izolacja wełniana", 1d, 10d),
    STYROFOAM("Styropian", 1d, 10d),
    WOOD_PLATES("Płyty drewniane", 1d, 10d),
    RAW_WOOD("Drewno surowe", 1d, 10d),
    STEEL_PIPE("Rura stalowa", 1d, 10d),
    COPPER_PIPE("Rura miedziana", 1d, 10d),
    PCV_PIPE("Rura PCV", 1d, 10d),
    GLUE("Klej", 1d, 10d),
    RED_PAINT("Czerwona farba", 1d, 10d),
    BLUE_PAINT("Niebieska farba", 1d, 10d),
    GREEN_PAIN("Zielona farba", 1d, 10d),
    WHITE_PAINT("Biała farba", 1d, 10d);

    private final String name;
    private final Double minPrice;
    private final Double maxPrice;

    private Double currentPrice;

    public Double generatePrice() {
        Double generatedPrice = ThreadLocalRandom.current().nextDouble(minPrice, maxPrice + 1);
        currentPrice = trimPrice(generatedPrice);
        return currentPrice;
    }

    private double trimPrice(Double price) {
        return ((double) ((int) (price * 100.0))) / 100.0;
    }
}
