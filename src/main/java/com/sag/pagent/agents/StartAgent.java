package com.sag.pagent.agents;

import com.sag.pagent.SampleAgent;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class StartAgent extends LoggerAgent {
    private transient AgentContainer agentContainer;

    @Override
    protected void setup() {
        log.info("start");
        addBehaviour(new StartupBehaviour(this, 100));

    }

    public AgentContainer getAgentContainer() {
        if (agentContainer == null) {
            agentContainer = getContainerController();
        }
        return agentContainer;
    }

    private class StartupBehaviour extends WakerBehaviour {
        public StartupBehaviour(Agent a, long timeout) {
            super(a, timeout);
        }

        @Override
        protected void onWake() {
            createNewAgents(SampleAgent.class, 1);
        }

        private void createNewAgents(final Class<?> clazz, int size) {
            try {
                for (int i = 0; i < size; ++i) {
                    String name = clazz.getSimpleName() + '-' + i;
                    String path = clazz.getName();
                    log.info("create agent: {}, from: {}", name, path);
                    AgentController agentController = getAgentContainer().createNewAgent(name, path, null);
                    agentController.start();
                }
            } catch (StaleProxyException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
