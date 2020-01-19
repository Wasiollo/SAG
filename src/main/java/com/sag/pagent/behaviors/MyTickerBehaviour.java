package com.sag.pagent.behaviors;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;

public abstract class MyTickerBehaviour extends SequentialBehaviour {
    public MyTickerBehaviour(Agent a, long timeout) {
        super(a);
        addSubBehaviour(new FirstTick(a));
        addSubBehaviour(new NextTicks(a, timeout));
    }

    protected abstract void onTick();

    private class FirstTick extends OneShotBehaviour {
        public FirstTick(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            MyTickerBehaviour.this.onTick();
        }
    }

    private class NextTicks extends TickerBehaviour {
        public NextTicks(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            MyTickerBehaviour.this.onTick();
        }
    }
}
