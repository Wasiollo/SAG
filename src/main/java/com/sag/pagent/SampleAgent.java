package com.sag.pagent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleAgent extends Agent {
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
