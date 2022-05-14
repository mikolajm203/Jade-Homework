package com.Agents;

import com.Enums.Cuisines;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent{
    Cuisines preference;
    @Override
    protected void setup(){
        super.setup();
        Object[] args = getArguments();
        preference = Cuisines.valueOf((String)args[0]);
        addBehaviour(askForRestaurants);
    }

    private Behaviour askForRestaurants = new OneShotBehaviour(this){

        @Override
        public void action() {
            //TODO: figure it out
            ACLMessage msg= new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("Peter", AID.ISLOCALNAME));
            msg.setLanguage("English");
            msg.setOntology("Weather-Forecast-Ontology");
            msg.setContent("Today it’s raining");
            send(msg);
        }
    };
}
