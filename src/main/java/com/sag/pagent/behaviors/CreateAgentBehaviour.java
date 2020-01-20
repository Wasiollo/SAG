package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateAgentBehaviour extends WakerBehaviour {
    private Class<?> clazz;
    private int amount;

    public CreateAgentBehaviour(Agent a, long timeout, final Class<?> clazz, int amount) {
        super(a, timeout);
        this.clazz = clazz;
        this.amount = amount;
    }

    @Override
    protected void onWake() {
        try {
            for (int i = 0; i < amount; ++i) {
                String name = clazz.getSimpleName() + '-' + i;
                String path = clazz.getName();
                createNewAgents(name, path);
            }
        } catch (StaleProxyException ex) {
            log.error("StaleProxyException occured", ex);
        }
    }

    private void createNewAgents(String name, String path) throws StaleProxyException {
        log.info("create agent: {}, from: {}", name, path);
        AgentController agentController = myAgent.getContainerController().createNewAgent(name, path, null);
        agentController.start();
    }
}