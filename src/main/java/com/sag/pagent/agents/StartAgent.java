package com.sag.pagent.agents;

import com.sag.pagent.behaviors.CreateAgentBehaviour;
import com.sag.pagent.broker.BrokerAgent;
import com.sag.pagent.customer.CustomerAgent;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartAgent extends Agent {
    @Override
    protected void setup() {
        log.info("start");
        addBehaviour(new CreateAgentBehaviour(this, 100, BrokerAgent.class, 2));
        addBehaviour(new CreateAgentBehaviour(this, 200, ShopAgent.class, 4));
        addBehaviour(new CreateAgentBehaviour(this, 300, CustomerAgent.class, 1));
    }
}
