package com;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public class ClientAgent extends Agent{
    @Override
    protected void setup(){
        super.setup();
        addBehaviour(xd);
    }

    private Behaviour xd = new OneShotBehaviour(this){

        @Override
        public void action() {
            System.out.println("Hello World, my name is xd");
        }
    };
}
