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
		private AID firstAID;
		private int bestDeskPrice;
		private int secondDeskPrice;
		
		public DataForCalculatingBidValue(AID firstAID, int bestDeskPrice, int secondDeskPrice)
		{
			this.firstAID = firstAID;
			this.bestDeskPrice = bestDeskPrice;
			this.secondDeskPrice = secondDeskPrice;
		}
		
		public AID getFirstAID()
		{
			return firstAID;
		}
		
		public int getBestDeskPrice()
		{
			return bestDeskPrice;
		}
		
		public int getSecondDeskPrice()
		{
			return secondDeskPrice;
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
		DataForCalculatingBidValue bidData = findTwoBestDesks();
		int bidValue = bidData.getBestDeskPrice() - bidData.getSecondDeskPrice() + EPSILON;
		int employeeMoney = ((EmployeeAgent)myAgent).getAmountOfMoney();
		if (bidValue > employeeMoney)
		{
			// TODO nie mozemy wystawic bid bo przekracza nasze mozliwosci pieniedzy
			// Zastanowic sie czy to tak ma byc czy jakos inaczej ...
			((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.NOT_ENOUGH_MONEY_TO_BID_PREFERRED_DESK);
		}
		else // TODO wystawiamy bid nowy do najlepszego biurka :)
			sendMessage(bidData.firstAID, "bid", ACLMessage.PROPOSE);
	}
	
	private DataForCalculatingBidValue findTwoBestDesks()
	{	// TODO do zastanowienia sie czy nie przerobic kodu i zrobic by niektore z tych rzeczy byly nie w pracowniku tylko w zachowaniu
		AID[] preferredDesksAIDs = ((EmployeeAgent)myAgent).getPreferredDesksAIDs();
		HashMap<AID, Integer> prices = (HashMap<AID, Integer>) ((EmployeeAgent)myAgent).getDesksPrices();
		AID bestDesk = preferredDesksAIDs[0];
		int numberOfPreferredDesks = EmployeeAgent.NUMBER_OF_PREFERRED_DESKS;
		int[] maxDeskPrices = EmployeeAgent.getMaxDeskPrices();
		int employeeGainForDesk;
		int bestDeskGain = -1, secondDeskGain = -1;
		AID currentDeskAID;
		for (int preferredDeskIndex = 0; preferredDeskIndex < numberOfPreferredDesks; preferredDeskIndex++)
		{
			currentDeskAID = preferredDesksAIDs[preferredDeskIndex];
			employeeGainForDesk = maxDeskPrices[preferredDeskIndex] - prices.get(currentDeskAID);
			
			if (bestDeskGain < employeeGainForDesk)
			{
				bestDesk = currentDeskAID;
				secondDeskGain = bestDeskGain;
				bestDeskGain = employeeGainForDesk;
			}
			else if (secondDeskGain < employeeGainForDesk)
				secondDeskGain = employeeGainForDesk;
		}
		
		return (new DataForCalculatingBidValue(bestDesk, bestDeskGain, secondDeskGain));		
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