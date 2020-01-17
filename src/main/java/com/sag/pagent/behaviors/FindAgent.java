package com.sag.pagent.behaviors;

import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


public class FindAgent extends TickerBehaviour {
    protected final transient Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    private AgentFoundListener agentFoundListener;
    private final ServiceType serviceType;

    public interface AgentFoundListener extends Serializable {
        public void agentFound(AID agent);
    }

    public FindAgent(Agent a, long timeout, AgentFoundListener agentFoundListener, ServiceType serviceType) {
        super(a, timeout);
        this.agentFoundListener = agentFoundListener;
        this.serviceType = serviceType;
    }

    @Override
    protected void onTick() {
        AID agent = ServiceUtils.findAgent(myAgent, serviceType);

        if (agent == null) {
            log.debug("{} agent not found", serviceType);
            return;
        }
        log.debug("{} agent found", serviceType);
        myAgent.removeBehaviour(this);
        agentFoundListener.agentFound(agent);
    }
}
