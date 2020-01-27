package com.sag.pagent.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ServiceType {
    CUSTOMER("customer"),
    BROKER("broker"),
    SHOP("shop"),
    MANAGER("manager");

    private final String type;
}
