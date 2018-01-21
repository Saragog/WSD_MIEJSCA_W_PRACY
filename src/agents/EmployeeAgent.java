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
	private Map<AID, Price> mapOfDeskPrices;
	private int deskCount;
	
	public static final int NUMBER_OF_PREFERRED_DESKS = 4;
	
	protected void setup() { // Jako 1 argument to AID wszystkich biurek 2 argument AID z preferowanymi stolami 3 argument to ilosc pieniedzy dla pracownika 
		
		Object[] args = getArguments();
		allDesks = (AID[])args[0];
		deskCount = allDesks.length;
		deduceAllDeskMaxBidTokenParts(args[1]);
		amountOfMoney = (int)args[2];
		
		mapOfDeskPrices = new HashMap<AID, Price> ();		
		this.state = EmployeeState.HAS_NO_DESK_TAKEN;
		addBehaviour(new behaviours.EmployeeBehaviour());
	}
	
	private void deduceAllDeskMaxBidTokenParts(Object preferedDeskIndices)
	{
    	System.out.println("Prefered indices " + Arrays.toString((Integer[])preferedDeskIndices) + getLocalName());
		int position;
		allDeskMaxBidTokenParts = new float[deskCount];
		for (int deskIndex = 0; deskIndex < deskCount; deskIndex++)
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

	public Map<AID, Price> getMapOfDeskPrices() {
		return mapOfDeskPrices;
	}
	
	public Price[] getDeskPricesAsArray()
	{
		Price[] deskPrices = new Price[deskCount];
		for (int index = 0; index < deskCount; index++)
			deskPrices[index] = mapOfDeskPrices.get(allDesks[index]);
		return deskPrices;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
