package agents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import utils.Price;



public class EmployeeAgent extends Agent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int amountOfMoney;
	private EmployeeState state;
	private AID[] allDesks;
	private static int[] preferredDeskMaxBidTokenPercentages;
	private float[] allDeskMaxBidTokenParts;
	private Map<AID, Price> desksPrices;
	
	public static final int NUMBER_OF_PREFERRED_DESKS = 4;
	
	protected void setup() { // Jako 1 argument AID z preferowanymi stolami 2 argument to AID wszystkich biurek
		
		Object[] args = getArguments();
				
		
		// TODO LEKKIE ZMIANY W KOLEJNOSCI ARGUMENTOW ???
		
		
		allDesks = (AID[])args[1];
		deduceAllDeskMaxBidTokenParts(args[0]);
		amountOfMoney = (int)args[2];
		//System.out.println(allDeskMaxBidTokenParts);

		desksPrices = new HashMap<AID, Price> ();
		
		this.state = EmployeeState.HAS_NO_DESK_TAKEN;
		
		
		addBehaviour(new behaviours.EmployeeBehaviour());
	}
	
	private void deduceAllDeskMaxBidTokenParts(Object preferedDeskIndices)
	{
    	System.out.println("Prefered indices " + Arrays.toString((Integer[])preferedDeskIndices) + getLocalName());
		
		int len = allDesks.length, position;
		allDeskMaxBidTokenParts = new float[len];
		for (int deskIndex = 0; deskIndex < len; deskIndex++)
		{
			position = Arrays.asList((Integer[])preferedDeskIndices).indexOf(Integer.valueOf(deskIndex+1));
			if (position == -1) allDeskMaxBidTokenParts[deskIndex] = (float)0.0;
			else allDeskMaxBidTokenParts[deskIndex] = (float) ((float)preferredDeskMaxBidTokenPercentages[position] / 100.0);	
		}
	}

	public static void setPreferredDeksMaxBidTokenPercentages(int[] preferredDeskMaxBidTokenPercentages)
	{
		EmployeeAgent.preferredDeskMaxBidTokenPercentages = preferredDeskMaxBidTokenPercentages;		
	}
	
	public float[] getAllDeskMaxBidTokenParts()
	{
		return allDeskMaxBidTokenParts;
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

	public static void setPreferredDeskMaxBidTokenPercentages(int[] preferredDeskMaxBidTokenPercentages){
		EmployeeAgent.preferredDeskMaxBidTokenPercentages = preferredDeskMaxBidTokenPercentages;
	}

	public AID[] getAllDesks() {
		return allDesks;
	}

	public void setAllDesks(AID[] allDesks) {
		this.allDesks = allDesks;
	}

	public Map<AID, Price> getDesksPrices() {
		return desksPrices;
	}

	public void setDesksPrices(Map<AID, Price> desksPrices) {
		this.desksPrices = desksPrices;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
