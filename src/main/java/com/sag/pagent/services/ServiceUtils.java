package com.sag.pagent.services;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class ServiceUtils {
    private ServiceUtils() {
    }

    public static AID findAgent(Agent agent, ServiceType type) {
        log.trace("findAgent, type: {} ", type);
        return findAgentList(agent, type, 1).get(0);
    }

    public static List<AID> findAgentList(Agent agent, ServiceType type, int maxAgents) {
        log.trace("findAgentList, type: {}, maxAgents: {}", type, maxAgents);
        List<AID> agentList = findAgentList(agent, type);
        List<AID> newList = new LinkedList<>();
        for (int i = 0; i < maxAgents && !agentList.isEmpty(); i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(agentList.size());
            newList.add(agentList.remove(randomIndex));
        }
        return newList;
    }

    public static List<AID> findAgentList(Agent agent, ServiceType type) {
        log.trace("findAgentList, type: {} ", type);
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type.getType());
        template.addServices(sd);
        try {
            return Arrays.stream(DFService.search(agent, template))
                    .map(DFAgentDescription::getName)
                    .collect(Collectors.toList());
        } catch (FIPAException fe) {
            log.error("FIPAException in findAgentList", fe);
        }
        return new LinkedList<>();
    }
}
