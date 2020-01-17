package com.sag.pagent.shop;

import java.io.Serializable;
import java.util.Random;

public enum ShopGoods implements Serializable {
    PASTA(3.0, 5.0),
    RICE(2.0, 5.0),
    BREAD(1.0, 3.0);

    private double minPrice;
    private double maxPrice;
    private static Random random = new Random();

    ShopGoods(double minPrice, double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public double getRandomPrice() {
        return minPrice + (maxPrice - minPrice) * random.nextDouble();
    }
}
