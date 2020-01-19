package com.sag.pagent.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;

@RequiredArgsConstructor
@Getter
public class PurchaseOrder implements java.io.Serializable {
    private final String customerAgentId;
    private final LinkedList<String> brokerAgentIdList;
}
