package com.sag.pagent.agents;

import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class LoggerAgent extends Agent {
    protected final transient Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
}
