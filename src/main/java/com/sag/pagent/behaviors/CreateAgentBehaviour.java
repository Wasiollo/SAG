package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAgentBehaviour extends WakerBehaviour {
    protected final transient Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    private Class<?> clazz;
    private int size;

    public CreateAgentBehaviour(Agent a, long timeout, final Class<?> clazz, int size) {
        super(a, timeout);
        this.clazz = clazz;
        this.size = size;
    }

    @Override
    protected void onWake() {
        try {
            for (int i = 0; i < size; ++i) {
                String name = clazz.getSimpleName() + '-' + i;
                String path = clazz.getName();
                createNewAgents(name, path);
            }
        } catch (StaleProxyException ex) {
            log.error(ex.getMessage());
        }
    }

    private void createNewAgents(String name, String path) throws StaleProxyException {
        log.info("create agent: {}, from: {}", name, path);
        AgentController agentController = myAgent.getContainerController().createNewAgent(name, path, null);
        agentController.start();
    }
}