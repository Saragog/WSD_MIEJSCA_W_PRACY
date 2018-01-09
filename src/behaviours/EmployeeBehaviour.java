package behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utils.Price;
import agents.EmployeeState;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import agents.*;

public class EmployeeBehaviour extends CyclicBehaviour{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int priceResponseCounter = 0;

	// private static final int EPSILON = 1; // TODO aktualnie nie wykorzystany omowic to co mowil Palka
	
	private class DataForCalculatingBidValue
	{		
		private Price bidIncrement;
		private Price[] preferredDeskPrices;
		
		private Integer[] deskIndexesInOrderByGains; 
		
		public DataForCalculatingBidValue(AID[] deskAIDs, HashMap<AID, Price> mapOfDeskPrices)
		{
			int[] maxDeskTokens = EmployeeAgent.getMaxDeskTokens();
			preferredDeskPrices = readPreferredDeskPricesFromMap(deskAIDs, mapOfDeskPrices);
			
			Price[] deskGains = calculateDeskGains(maxDeskTokens, preferredDeskPrices); // wartosci Z
			int len = maxDeskTokens.length;
			
			deskIndexesInOrderByGains = new Integer[len];// = sortDeskGains(deskGains);
			
			for (int x = 0; x < len; x++)
				deskIndexesInOrderByGains[x] = x;

			Arrays.sort(deskIndexesInOrderByGains, new Comparator<Integer>() {
				public int compare(Integer first, Integer second)
				{
					return deskGains[first].compareTo(deskGains[second]);
				}
			});
			
			int bestDeskIndex = deskIndexesInOrderByGains[0], secondDeskIndex = deskIndexesInOrderByGains[1];
			int bidTokens = deskGains[bestDeskIndex].tokens - deskGains[secondDeskIndex].tokens;
			int bidEpsilons = deskGains[bestDeskIndex].epsilons - deskGains[secondDeskIndex].epsilons + 1;
			
			bidIncrement = new Price(bidTokens, bidEpsilons);	
		}
		
		private int[] sortDeskGains(Price[] deskGains)
		{
			int len = deskGains.length;
			int[] deskIndexesInOrderByGains = new int[len];
			
			
			
			return deskIndexesInOrderByGains;
		}
		
		private Price[] readPreferredDeskPricesFromMap(AID[] deskAIDs, HashMap<AID, Price> mapOfDeskPrices)
		{
			int len = deskAIDs.length;
			Price[] preferredDeskPrices = new Price[len];
			for (int index = 0; index < len; index++)
				preferredDeskPrices[index] = mapOfDeskPrices.get(deskAIDs[index]);
			return preferredDeskPrices;
		}
		
		// TODO pozamieniac te max Desk prices na max desk tokens czy cos by nie mylilo sie
		private Price[] calculateDeskGains(int[] maxDeskTokens, Price[] deskPrices)
		{
			int len = deskPrices.length;
			int deskGainTokens, deskGainEpsilons;
			Price[] deskGains = new Price[len];
			for (int deskIndex = 0; deskIndex < len; deskIndex++)
			{
				deskGainTokens = maxDeskTokens[deskIndex] - deskPrices[deskIndex].tokens;
				deskGainEpsilons = -deskPrices[deskIndex].epsilons;
				deskGains[deskIndex].tokens = deskGainTokens;
				deskGains[deskIndex].epsilons = deskGainEpsilons;
			}
			return deskGains;
		}
		
		public Price getBidIncrement()
		{
			return bidIncrement;
		}
		
		public Price[] getPreferredDeskPrices()
		{
			return preferredDeskPrices;
		}
		
		public Integer[] getDeskIndexesInOrderByGains()
		{
			return deskIndexesInOrderByGains;
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
						adjustPreferredDeskPrice(sender, new Price(Integer.parseInt(contentsParts[1]), Integer.parseInt(contentsParts[2]))); 
					

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
		Price bidIncrement = bidData.getBidIncrement();
		
		Price[] preferredDeskPrices = bidData.getPreferredDeskPrices();
		int len = preferredDeskPrices.length;
		int employeeMoney = ((EmployeeAgent)myAgent).getAmountOfMoney();
		Integer[] deskIndexesInOrderByGains = bidData.getDeskIndexesInOrderByGains();
		AID[] preferredDesksAIDs = ((EmployeeAgent)myAgent).getPreferredDesksAIDs();
		
		int currentPriceTokens, currentPriceEpsilons;
		Price currentPrice;
		String messageContents = "";
		for (int deskIndex = 0; deskIndex < len; deskIndex++)
		{
			currentPrice = preferredDeskPrices[deskIndexesInOrderByGains[deskIndex]];
			currentPriceTokens = currentPrice.tokens;
			currentPriceEpsilons = currentPrice.epsilons; 
			messageContents = "bid" + ":" + currentPriceTokens + bidIncrement.tokens + ":" + currentPriceEpsilons + bidIncrement.epsilons;
			if (preferredDeskPrices[deskIndexesInOrderByGains[deskIndex]].tokens < employeeMoney)
			{
				sendMessage(preferredDesksAIDs[deskIndexesInOrderByGains[deskIndex]], messageContents, ACLMessage.PROPOSE); // bidujemy :)
				break;
			}
		}
		
		((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.NOT_ENOUGH_MONEY_TO_BID_PREFERRED_DESK); // nie udalo sie nic zabidowac :(
	}
		
	private DataForCalculatingBidValue preparePreferredDesksData()
	{	// TODO do zastanowienia sie czy nie przerobic kodu i zrobic by niektore z tych rzeczy byly nie w pracowniku tylko w zachowaniu
		AID[] preferredDesksAIDs = ((EmployeeAgent)myAgent).getPreferredDesksAIDs();
		HashMap<AID, Price> prices = (HashMap<AID, Price>) ((EmployeeAgent)myAgent).getDesksPrices();
		
		DataForCalculatingBidValue data = new DataForCalculatingBidValue(preferredDesksAIDs, prices);
		
		return data;	
	}
	
	private void adjustPreferredDeskPrice(AID sender, Price price)
	{
		HashMap<AID, Price> prices = (HashMap<AID, Price>) ((EmployeeAgent)myAgent).getDesksPrices();
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