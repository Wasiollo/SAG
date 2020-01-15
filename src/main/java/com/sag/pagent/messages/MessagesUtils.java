package com.sag.pagent.messages;

import jade.lang.acl.ACLMessage;

import java.util.UUID;

public class MessagesUtils {
    static public ACLMessage createMessage(int perf) {
        ACLMessage msg = new ACLMessage(perf);
        msg.setConversationId(generateRandomStringByUUIDNoDash());
        return msg;
    }

    static public String generateRandomStringByUUIDNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

