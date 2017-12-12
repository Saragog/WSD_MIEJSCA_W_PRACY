package agents;

import jade.core.Agent;

import java.util.*;

public class DeskAgent extends Agent {
	
	/* Pytanie: Jakie informacje o sobie i o innych agentach
	 * powinnien mieć DeskAgent.
	 * id? Lista agentów? 
	*/
	
	protected void setup() {
		
		System.out.println("Siema tutaj agent: " + getAID().getName()+" jestem gotowy!!!");
		
		addBehaviour(new behaviours.ExampleBehaviour());
		
		// Bahaviour, ktory odpowiada na zapytanie o aktualna cele biorka
		addBehaviour(new deskBehaviours.CurrentDeskPrice());
		
		// Bahaviour, ktory przyjmuje cene zaproponowana przez pracownika
		// i odpowiada czy cena zostala przyjeta
		addBehaviour(new deskBehaviours.BidAuction());
		
		
	}
	
	

}
