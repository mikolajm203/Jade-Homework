package com.Agents;

import com.Enums.Cuisines;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.security.KeyPair;
import java.util.*;

import static java.lang.Integer.parseInt;

public class ClientAgent extends Agent{
    int messagesReceivedCount;
    private Cuisines preference;
    private String meal;
    private int timeInHours;
    private int noOfPeople;
    private List<AID> gatewayAgents;
    private AID selectedRestaurant;
    private int price;
    @Override
    protected void setup(){
        super.setup();
        price = Integer.MAX_VALUE;
        messagesReceivedCount = 0;
        selectedRestaurant = null;
        meal = "Pizza ";
        timeInHours = 2;
        noOfPeople = 2;

        Object[] args = getArguments();
        if(Objects.nonNull(args) && args.length == 1){
            preference = Cuisines.valueOf(args[0].toString());
        }
        getProperGatewayAgents();
        if(gatewayAgents.size() == 0){
            System.out.printf("%s no good restaurants nearby :c\n",
                    getAID().getName());
            doDelete();
        }
        addBehaviour(askForRestaurants);
        addBehaviour(readMessages);
    }
    @Override
    protected void takeDown(){
        System.out.printf("%s I'm dead, bye ;)\n", getAID().getName());
    }
    private void getProperGatewayAgents(){
        gatewayAgents = new ArrayList<AID>();
        DFAgentDescription agentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(preference.name());
        agentDescription.addServices(serviceDescription);

        try{
            DFAgentDescription[] agents = DFService.search(this, agentDescription);
            Arrays.stream(agents).forEach(dfAgentDescription -> gatewayAgents.add(dfAgentDescription.getName()));
        }
        catch(FIPAException e){
            e.printStackTrace();
        }
    }
    private Behaviour askForRestaurants = new OneShotBehaviour(this){

        @Override
        public void action() {
            ACLMessage msg= new ACLMessage(ACLMessage.CFP);
            String content = meal + timeInHours + " " + noOfPeople;
            msg.setContent(content);
            for (var agent:
                 gatewayAgents) {
                System.out.printf("%s sending cfp to %s\n",
                        myAgent.getAID().getName(),
                        agent.getName());
                msg.addReceiver(agent);
            }
            send(msg);
        }
    };

    private Behaviour readMessages = new CyclicBehaviour(this) {
        MessageTemplate tpl = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(tpl);
            if(Objects.nonNull(msg)){
                // If received price check if it's the lowest one
                if(!msg.getContent().isEmpty()){
                    int proposedPrice = parseInt(msg.getContent());
                    if(proposedPrice < price){
                        selectedRestaurant = msg.getSender();
                        price = proposedPrice;
                    }
                }
                messagesReceivedCount += 1;
                // If got all the gatewayAgents to respond,
                // buy a meal from the restaurant with the lowest price
                if(messagesReceivedCount == gatewayAgents.size()){
                    if(Objects.nonNull(selectedRestaurant)){
                        System.out.printf("%s made a reservation at %s\n",
                                myAgent.getAID().getName(),
                                selectedRestaurant.getName());
                    }
                    else{
                        System.out.printf("%s no good restaurants nearby :c\n",
                                myAgent.getAID().getName());
                    }
                    myAgent.doDelete();
                }
            }
            else{
                block();
            }
        }
    };
}
