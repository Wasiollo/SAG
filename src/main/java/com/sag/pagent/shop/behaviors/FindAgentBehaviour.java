package com.sag.pagent.shop.behaviors;

import com.sag.pagent.behaviors.MyTickerBehaviour;
import com.sag.pagent.services.ServiceType;
import com.sag.pagent.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FindAgentBehaviour extends MyTickerBehaviour {
    private final ServiceType serviceType;
    private final int maxAgents;
    private AgentFoundListener agentFoundListener;

    public FindAgentBehaviour(Agent a, long timeout, AgentFoundListener agentFoundListener, ServiceType serviceType, int maxAgents) {
        super(a, timeout);
        this.agentFoundListener = agentFoundListener;
        this.serviceType = serviceType;
        this.maxAgents = maxAgents;
    }

    @Override
    protected void onTick() {
        List<AID> agentList = ServiceUtils.findAgentList(myAgent, serviceType, maxAgents);

        if (agentList.isEmpty()) {
            log.debug("{} agentList not found", serviceType);
            return;
        }
        log.debug("{} agentList found: {}", serviceType, agentList.stream().map(AID::getLocalName).collect(Collectors.toList()));
        myAgent.removeBehaviour(this);
        agentFoundListener.agentFound(agentList);
    }

    public interface AgentFoundListener extends Serializable {
        void agentFound(List<AID> agentList);
    }
}
