package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;



public class EmployeeAgent extends Agent{
	protected void setup() {
		
		addBehaviour(new behaviours.EmployeeBehaviour());
		
		System.out.println("Siema tutaj agent: " + getAID().getName()+" jestem gotowy!!!");
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("Miejsce do siedzenia na tylku1",AID.ISLOCALNAME));
		msg.setLanguage("jezykWSD");
		msg.setOntology("OntologiaPrawdy");
		//msg.setPerformative(perf);
		msg.setContent("tresc testowa");
		
		send(msg);
		
	}
}
