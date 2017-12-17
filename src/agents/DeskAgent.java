package agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.Agent;
import jade.core.AID;

public class DeskAgent extends Agent {
	
	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = 1L;

	private int currentPrice;
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
		allDesks = (AID[])args[1];
		this.currentPrice = 0;
		this.state = DeskState.FREE;
		employeeList = new LinkedList<AID>();
		System.out.println("Siema tutaj agent: " + getAID().getName()+" jestem gotowy!!!");
		addBehaviour(new behaviours.DeskBehaviour());
		
	}

	public int getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(int currentPrice) {
		this.currentPrice = currentPrice;
	}
	
	public DeskState getDeskState() {
		return state;
	}

	public void setDeskState(DeskState state) {
		this.state = state;
	}

}