package behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import agents.EmployeeState;

import java.util.HashMap;

import agents.*;

public class EmployeeBehaviour extends CyclicBehaviour{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int priceResponseCounter = 0;

	private static final int EPSILON = 1; // TODO do obmyslenia jest to epsilon do wyliczania bid
	
	private class DataForCalculatingBidValue
	{
		private int bidIncrement;
		private int[] preferredDeskPrices;
		
		public DataForCalculatingBidValue(AID[] deskAIDs, HashMap<AID, Integer> mapOfDeskPrices)
		{
			int[] maxDeskPrices = EmployeeAgent.getMaxDeskPrices();
			preferredDeskPrices = readPreferredDeskPricesFromMap(deskAIDs, mapOfDeskPrices);
			int[] deskGains = calculateDeskGains(maxDeskPrices, preferredDeskPrices); // wartosci Z
			if (deskGains[0] > deskGains[1])
				bidIncrement = deskGains[0] - deskGains[1] + EPSILON;
			else
				bidIncrement = EPSILON;			
		}
		
		private int[] readPreferredDeskPricesFromMap(AID[] deskAIDs, HashMap<AID, Integer> mapOfDeskPrices)
		{
			int len = deskAIDs.length;
			int[] preferredDeskPrices = new int[len];
			for (int index = 0; index < len; index++)
				preferredDeskPrices[index] = mapOfDeskPrices.get(deskAIDs[index]);
			return preferredDeskPrices;
		}
		
		private int[] calculateDeskGains(int[] maxDeskPrices, int[] deskPrices)
		{
			int len = deskPrices.length;
			int[] deskGains = new int[len];
			for (int deskIndex = 0; deskIndex < len; deskIndex++)
				deskGains[deskIndex] = maxDeskPrices[deskIndex] - deskPrices[deskIndex]; 
			return deskGains;
		}
		
		public int getBidIncrement()
		{
			return bidIncrement;
		}
		
		public int[] getPreferredDeskPrices()
		{
			return preferredDeskPrices;
		}
	}
	
	public void action() {
		
		
		EmployeeState agentState = ((EmployeeAgent)myAgent).getEmployeeState();
		String agentName = myAgent.getAID().getName();
		
		switch(agentState)
		{
			case HAS_NO_DESK_TAKEN:
			{
				askPreferredDesksAboutPrice();
				break;
			}
			case HAS_DESK_TAKEN:
			{
				ACLMessage msg = myAgent.receive();
				
				if (msg != null	) 
				{
			
					System.out.println("wiadomosc: "+msg.getContent());
					
				}
				else
				{
					block();
				}
				break;
			}
			case WAITING_FOR_PRICE_RESPONSES:
			{
				ACLMessage msg = myAgent.receive();
				
				if (msg != null	) 
				{
					String messageContent = msg.getContent();
					String[] contentsParts = messageContent.split(":");
					AID sender = msg.getSender();
					if (contentsParts[0] == "price")
						adjustPreferredDeskPrice(sender, Integer.parseInt(contentsParts[1]));
					

					System.out.println(agentName + " otrzymal wiadomosc: "+msg.getContent());
					
				}
				else
				{
					block();
				}
				
				break;
			}
			case CALCULATING_NEW_OFFER:
			{
				makeOffer();				
				break;
			}
		}    
		
	}
	
	// TODO sprawdzcie prosze czy to wam pasuje.
	private void makeOffer()
	{
		DataForCalculatingBidValue bidData = preparePreferredDesksData();
		int bidIncrement = bidData.getBidIncrement();
		int[] preferredDeskPrices = bidData.getPreferredDeskPrices();
		int len = preferredDeskPrices.length;
		int employeeMoney = ((EmployeeAgent)myAgent).getAmountOfMoney();
		AID[] preferredDesksAIDs = ((EmployeeAgent)myAgent).getPreferredDesksAIDs();
		for (int preferredDeskIndex = 0; preferredDeskIndex < len; preferredDeskIndex++)
		{
			if (preferredDeskPrices[preferredDeskIndex] + bidIncrement < employeeMoney)
			{
				sendMessage(preferredDesksAIDs[preferredDeskIndex], "bid", ACLMessage.PROPOSE); // bidujemy :)
				break;
			}
		}
		
		((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.NOT_ENOUGH_MONEY_TO_BID_PREFERRED_DESK); // nie udalo sie nic zabidowac :(
	}
		
	private DataForCalculatingBidValue preparePreferredDesksData()
	{	// TODO do zastanowienia sie czy nie przerobic kodu i zrobic by niektore z tych rzeczy byly nie w pracowniku tylko w zachowaniu
		AID[] preferredDesksAIDs = ((EmployeeAgent)myAgent).getPreferredDesksAIDs();
		HashMap<AID, Integer> prices = (HashMap<AID, Integer>) ((EmployeeAgent)myAgent).getDesksPrices();
		
		DataForCalculatingBidValue data = new DataForCalculatingBidValue(preferredDesksAIDs, prices);
		
		return data;	
	}
	
	private void adjustPreferredDeskPrice(AID sender, int price)
	{
		HashMap<AID, Integer> prices = (HashMap<AID, Integer>) ((EmployeeAgent)myAgent).getDesksPrices();
		if (prices.containsKey(sender))
			prices.put(sender, price);
				
		// response na doslanie ceny ack ok
		sendMessage(sender, "ackOk", ACLMessage.INFORM);
		
		priceResponseCounter--;
		if (priceResponseCounter < 1)
			((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.CALCULATING_NEW_OFFER);
	}
	
	private void askPreferredDesksAboutPrice()
	{
		AID[] preferredDesksAIDs = ((EmployeeAgent)myAgent).getPreferredDesksAIDs();
		AID[] allDesks = ((EmployeeAgent)myAgent).getAllDesks();
		int preferredDesksCount = allDesks.length;
		
		String content = "priceQuestion";
		
		for (int x = 0; x < preferredDesksCount; x++)
			sendMessage(preferredDesksAIDs[x], content, ACLMessage.INFORM_REF);
		
		priceResponseCounter = preferredDesksCount;
		((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.WAITING_FOR_PRICE_RESPONSES);
	}
	
	private void sendMessage(AID receiver, String content, int performative)
	{
		ACLMessage messageToBeSent = new ACLMessage(performative);
		messageToBeSent.addReceiver(receiver);
		messageToBeSent.setContent(content);		
		myAgent.send(messageToBeSent);
	}
}