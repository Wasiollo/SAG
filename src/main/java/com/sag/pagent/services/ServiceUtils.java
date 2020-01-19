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
        List<AID> agentList = findAgentList(agent, type);
        int size = agentList.size();
        int index = ThreadLocalRandom.current().nextInt(size) % size;
        return agentList.get(index);
    }

    public static List<AID> findAgentList(Agent agent, ServiceType type) {
        log.trace("findAgentList, type: {} ", type);
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type.getType());
        template.addServices(sd);
        List<AID> agents = new LinkedList<>();
        try {
            agents = Arrays.stream(DFService.search(agent, template))
                    .map(DFAgentDescription::getName)
                    .collect(Collectors.toList());
        } catch (FIPAException fe) {
            log.error(fe.getMessage());
        }
        return agents;
    }
}
