package com.sag.pagent.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisterShopAgent implements java.io.Serializable {
    private final String shopAgentId;
}
