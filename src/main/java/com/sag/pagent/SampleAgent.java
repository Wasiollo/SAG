package com.sag.pagent;

import com.sag.pagent.agents.LoggerAgent;
import jade.core.behaviours.TickerBehaviour;

public class SampleAgent extends LoggerAgent {
    @Override
    protected void setup() {
        // Printout a welcome message
        log.info("Hello! Sample Agent {} is ready.", getAID().getName());
        addBehaviour(new TickerBehaviour(this, 1000) {
                         protected void onTick() {
                             log.debug("heartbeat!");
                         }
                     }
        );
    }
}
