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
	private long startTime = System.currentTimeMillis();
	private static final long minTime = 10000;
	private AID winningEmployee;
	


	private List<AID> employeeList; 
	private int desksTaken = 0;
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
	
	public int getDesksTaken(){
		return desksTaken;
	}
	
	public void setDesksTaken(int desksTaken){
		this.desksTaken = desksTaken;
	}
	
	public void addEmployeeToList(AID employee){
		employeeList.add(employee);
	}
	
	public int getAmountOfEmployees(){
		return employeeList.size();
	}
	
	public boolean minTimeElapsed(){
		return System.currentTimeMillis() - startTime >= minTime;
	}
	
	public AID getWinningEmployee(){
		return winningEmployee;
	}
	
	public void setWinningEmployee(AID employee){
		this.winningEmployee = employee;
	}

}