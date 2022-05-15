package com.Agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Objects;
import java.util.Random;

public class ManagerAgent extends Agent {
    @Override
    public void setup(){
        super.setup();
        addBehaviour(readMessages);
    }

    Behaviour readMessages = new CyclicBehaviour(this) {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if(Objects.nonNull(msg)){
                Random rand = new Random();
                if(false /*rand.nextBoolean()*/){
                    System.out.printf("%s can't accept the order :c\n",
                            myAgent.getAID().getName());
                    ACLMessage response = new ACLMessage(ACLMessage.REFUSE);
                    response.setConversationId(msg.getConversationId());
                    response.addReceiver(msg.getSender());
                    myAgent.send(response);
                }
                else{
                    System.out.printf("%s accepted the order\n",
                            myAgent.getAID().getName());
                    ACLMessage rresponse = new ACLMessage(ACLMessage.AGREE);
                    rresponse.setConversationId(msg.getConversationId());
                    rresponse.addReceiver(msg.getSender());
                    rresponse.setContent(String.valueOf(rand.nextInt(100)));
                    myAgent.send(rresponse);
                }
            }
            else{
                block();
            }
        }
    };
}
