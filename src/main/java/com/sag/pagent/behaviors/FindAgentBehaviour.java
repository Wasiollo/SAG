package com.sag.pagent.behaviors;

import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public class FindAgentBehaviour extends MyTickerBehaviour {
    private AgentFoundListener agentFoundListener;
    private final ServiceType serviceType;

    public interface AgentFoundListener extends Serializable {
        void agentFound(AID agent);
    }

    public FindAgentBehaviour(Agent a, long timeout, AgentFoundListener agentFoundListener, ServiceType serviceType) {
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
        log.debug("{} agent found: {}", serviceType, agent.getLocalName());
        myAgent.removeBehaviour(this);
        agentFoundListener.agentFound(agent);
    }
}
