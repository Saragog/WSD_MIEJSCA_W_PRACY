package agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.Agent;
import utils.Price;
import jade.core.AID;

public class DeskAgent extends Agent {
	
	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = 1L;

	private Price currentPrice;
	private DeskState state;
	private AID[] allDesks;
	


	private List<AID> employeeList; 
	/* 
	 * Pytanie: Jakie informacje o sobie i o innych agentach
	 * powinnien mieć DeskAgent.
	 * id? Lista agentów? 
	*/
	
	protected void setup() {
		Object[] args = getArguments();
		allDesks = (AID[])args[0];
		currentPrice = new Price();
		this.state = DeskState.FREE;
		employeeList = new LinkedList<AID>();
		System.out.println("Czesc tutaj agent: " + getAID().getName()+" jestem gotowy!!!");
		addBehaviour(new behaviours.DeskBehaviour());
		
	}

	public Price getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(Price currentPrice) {
		this.currentPrice = currentPrice;
	}
	
	public DeskState getDeskState() {
		return state;
	}

	public void setDeskState(DeskState state) {
		this.state = state;
	}

}