package agents;

import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;



public class EmployeeAgent extends Agent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int amountOfMoney;
	private EmployeeState state;
	// private int[] preferredDesksIndices;
	private AID[] preferredDesksAIDs;
	private AID[] allDesks;
	private Map<AID, Integer> desksPrices;
	
	protected void setup() { // Jako 1 argument AID z preferowanymi stolami 2 argument to AID wszystkich biurek
		
		Object[] args = getArguments();
		
		preferredDesksAIDs = (AID[])args[0];
		allDesks = (AID[])args[1];
		
		desksPrices = new HashMap<AID, Integer> ();
		
		this.state = EmployeeState.HAS_NO_DESK_TAKEN;
		
		addBehaviour(new behaviours.EmployeeBehaviour());
		
		System.out.println("Siema tutaj agent: " + getAID().getName()+" jestem gotowy!!!");
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.addReceiver(new AID("Miejsce do pracy 1",AID.ISLOCALNAME));
		msg.setLanguage("jezykWSD");
		msg.setOntology("OntologiaPrawdy");
		//msg.setPerformative(perf);
		msg.setContent("tresc testowa");
		send(msg);
		
		
	}

	public int getAmountOfMoney() {
		return amountOfMoney;
	}

	public void setAmountOfMoney(int amountOfMoney) {
		this.amountOfMoney = amountOfMoney;
	}

	public EmployeeState getEmployeeState() {
		return state;
	}

	public void setEmployeeState(EmployeeState state) {
		this.state = state;
	}

	public AID[] getPreferredDesksAIDs() {
		return preferredDesksAIDs;
	}

	public void setPreferredDesksAIDs(AID[] preferredDesksIndices) {
		this.preferredDesksAIDs = preferredDesksIndices;
	}

	public AID[] getAllDesks() {
		return allDesks;
	}

	public void setAllDesks(AID[] allDesks) {
		this.allDesks = allDesks;
	}

	public Map<AID, Integer> getDesksPrices() {
		return desksPrices;
	}

	public void setDesksPrices(Map<AID, Integer> desksPrices) {
		this.desksPrices = desksPrices;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
