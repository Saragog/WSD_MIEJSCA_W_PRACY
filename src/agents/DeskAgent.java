package agents;

import jade.core.Agent;

public class DeskAgent extends Agent {
	protected void setup() {
		System.out.println("Siema tutaj agent: " + getAID().getName()+" jestem gotowy!!!");
		addBehaviour(new behaviours.ExampleBehaviour());
		
	}
	
	

}
