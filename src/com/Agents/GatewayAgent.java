package com.Agents;

import com.Enums.Cuisines;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Objects;

public class GatewayAgent extends Agent {
    private Cuisines restaurantCuisine;
    private AID managerAgent;
    @Override
    public void setup(){
        Object[] args = getArguments();
        if(Objects.nonNull(args) && args.length == 2){
            restaurantCuisine = Cuisines.valueOf(args[0].toString());
            managerAgent = new AID(args[1].toString(), AID.ISLOCALNAME);
        }
        final DFAgentDescription DFdescription = new DFAgentDescription();
        DFdescription.setName(getAID());

        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(this.getAID().getName());
        serviceDescription.setType(restaurantCuisine.name());
        DFdescription.addServices(serviceDescription);
        try{
            DFService.register(this, DFdescription);
        }
        catch(FIPAException e){
            e.printStackTrace();
            doDelete();
        }
        System.out.printf("%s have been created with %s cuisine and manager %s\n",
                getAID().getName(),
                restaurantCuisine.name(),
                managerAgent.getName());
        addBehaviour(getMessages);
    }
    private Behaviour getMessages = new CyclicBehaviour(this) {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if(Objects.nonNull(msg)){
                switch(msg.getPerformative()){
                    case ACLMessage.CFP:
                        System.out.printf("%s received CFP from %s\n",
                                myAgent.getAID().getName(),
                                msg.getSender().getName());
                        ACLMessage query = new ACLMessage(ACLMessage.QUERY_IF);
                        query.addReceiver(managerAgent);
                        query.setConversationId(msg.getSender().getName());
                        myAgent.send(query);
                        break;
                    case ACLMessage.AGREE:
                        AID client = new AID(msg.getConversationId(), AID.ISGUID);
                        ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
                        response.addReceiver(client);
                        response.setContent(msg.getContent());
                        myAgent.send(response);
                        break;
                    case ACLMessage.REFUSE:
                        AID cclient = new AID(msg.getConversationId(), AID.ISLOCALNAME);
                        ACLMessage rresponse = new ACLMessage(ACLMessage.PROPOSE);
                        rresponse.addReceiver(cclient);
                        rresponse.setContent("");
                        myAgent.send(rresponse);
                        break;
                }
            }
            else{
                block();
            }
        }
    };
}
