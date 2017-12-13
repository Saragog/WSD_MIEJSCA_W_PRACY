package agents;

import jade.core.Agent;

public class DeskAgent extends Agent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* 
	 * Pytanie: Jakie informacje o sobie i o innych agentach
	 * powinnien mieć DeskAgent.
	 * id? Lista agentów? 
	*/
	
	protected void setup() {
		System.out.println("Siema tutaj agent: " + getAID().getName()+" jestem gotowy!!!");
		addBehaviour(new behaviours.DeskBehaviour());
		
	}
	
	

}