package agents;

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
	private AID[] preferredDesks;
	private Map<AID, Integer> desksPrices;
	
	public EmployeeAgent(int[] preferredDesksIndices, AID[] allDesks)
	{
		super();
		
	}
	
	
	protected void setup() { // Jako 1 argument AID z preferowanymi stolami 2 argument to AID wszystkich biurek
		
		Object[] args = getArguments();
		
		int[] preferredDesksIndices = args[0];
		
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
	
}
