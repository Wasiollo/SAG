package com.sag.pagent;

public class Constant {
    public static final Integer MAX_GENERATED_NEEDS = 2000;
    public static final Double MAX_GENERATED_BUDGET = 20000d;
    public static final Double MIN_GENERATED_BUDGET = 10000d;
    private static final Long TIME_TO_RESPOND = 5L * 1000;
    public static final Long PURCHASE_ORDER_TIME_TO_RESPOND = TIME_TO_RESPOND;
    public static final Long BUY_PRODUCTS_TIME_TO_RESPOND = TIME_TO_RESPOND;
    public static final Integer QUERY_ARTICLES_TIME = 10 * 1000;
    public static final Integer MAX_GENERATED_SUPPLIES = 300;
    public static final Integer REGENERATE_SUPPLIES_TIME = 60 * 1000;
    public static final int MAX_BROKERS_REGISTRATION_PER_SHOP = 1;
    public static final boolean SAMPLING_ALGORITHM = true;

    private Constant() {
    }
}
