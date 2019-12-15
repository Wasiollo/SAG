package com.sag.pagent.agents;

import com.sag.pagent.behaviors.CreateAgentBehaviour;

public class StartAgent extends LoggerAgent {
    @Override
    protected void setup() {
        log.info("start");
        addBehaviour(new CreateAgentBehaviour(this, 100, BrokerAgent.class, 2));
        addBehaviour(new CreateAgentBehaviour(this, 200, ShopAgent.class, 4));
        addBehaviour(new CreateAgentBehaviour(this, 300, CustomerAgent.class, 1));
    }
}
