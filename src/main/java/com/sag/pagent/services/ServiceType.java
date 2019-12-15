package com.sag.pagent.services;

public enum ServiceType {
    CUSTOMER("customer"),
    BROKER("broker"),
    SHOP("shop");

    String type;

    ServiceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
