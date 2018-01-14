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

	private int responseCount;
	private int allDesksCount;
	private int preferredDesksCount;
	
	// private static final int EPSILON = 1; // TODO aktualnie nie wykorzystany omowic to co mowil Palka
	
	private class DataForCalculatingBidValue
	{		
		private Price bidIncrement;
		private Price[] preferredDeskPrices;
		
		private Integer[] deskIndexesInOrderByGains; 
		
		public DataForCalculatingBidValue(AID[] deskAIDs, HashMap<AID, Price> mapOfDeskPrices)
		{
			int[] maxDeskTokens = EmployeeAgent.getMaxDeskTokens();
			preferredDesksCount = maxDeskTokens.length;

			preferredDeskPrices = readPreferredDeskPricesFromMap(deskAIDs, mapOfDeskPrices);
			allDesksCount = deskAIDs.length;
			Price[] deskGains = calculateDeskGains(maxDeskTokens, preferredDeskPrices); // wartosci Z
			
			deskIndexesInOrderByGains = new Integer[preferredDesksCount];
			
			for (int x = 0; x < preferredDesksCount; x++)
				deskIndexesInOrderByGains[x] = x;
			
			System.out.println("Wyznaczone wartosci Z: " + Arrays.toString(deskGains));
			
			System.out.println("Przed sortowaniem po Z: " + Arrays.toString(deskGains));
			
			Arrays.sort(deskIndexesInOrderByGains, new Comparator<Integer>() {
				public int compare(Integer first, Integer second)
				{
					return deskGains[first].compareTo(deskGains[second]);
				}
			});
			
			System.out.println("Po sortowaniu po Z: " + Arrays.toString(deskIndexesInOrderByGains));
			
			// 1 2 3 4
			// 100
			// 2 1 3 4
			// 100
			//
			// 2gi agent kupil biurko 2 za 25 + E
			// 
			// teraz stan jest taki:
			// 0 25+E 0 0
			// 100 75 50 25
			// 100 50-E 50 25
			// 100 50 50-E 25
			// 100 - 50 + E
			
			int bestDeskIndex = deskIndexesInOrderByGains[preferredDesksCount - 1], secondDeskIndex = deskIndexesInOrderByGains[preferredDesksCount - 2];
			int bidTokens = deskGains[bestDeskIndex].tokens - deskGains[secondDeskIndex].tokens;
			int bidEpsilons = deskGains[bestDeskIndex].epsilons - deskGains[secondDeskIndex].epsilons + 1;
			
			bidIncrement = new Price(bidTokens, bidEpsilons);	
		}
		
		private Price[] readPreferredDeskPricesFromMap(AID[] deskAIDs, HashMap<AID, Price> mapOfDeskPrices)
		{
			Price[] preferredDeskPrices = new Price[preferredDesksCount];
			for (int index = 0; index < preferredDesksCount; index++)
				preferredDeskPrices[index] = mapOfDeskPrices.get(deskAIDs[index]);
			return preferredDeskPrices;
		}
		
		private Price[] calculateDeskGains(int[] maxDeskTokens, Price[] deskPrices)
		{
			int deskGainTokens, deskGainEpsilons;
			Price[] deskGains = new Price[preferredDesksCount];
			for (int deskIndex = 0; deskIndex < preferredDesksCount; deskIndex++)
			{
				deskGainTokens = maxDeskTokens[deskIndex] - deskPrices[deskIndex].tokens;
				deskGainEpsilons = -deskPrices[deskIndex].epsilons;
				deskGains[deskIndex] = new Price(deskGainTokens, deskGainEpsilons);
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
					if (msg.getPerformative() == ACLMessage.INFORM)
					{
						if ( msg.getContent().equals("desk_overtaken"))
						{
							System.out.println("Przebicie stolka");
							((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.HAS_NO_DESK_TAKEN);
						}
						else if(msg.getContent().equals("End"))
						{
							System.out.println( myAgent.getLocalName() +" wygrywa " + msg.getSender().getLocalName() );
							((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.END);
						}
					}
					
					
					
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
					if (contentsParts[0].equals("price"))
					{
						adjustPreferredDeskPrice(sender, new Price(Integer.parseInt(contentsParts[1]), Integer.parseInt(contentsParts[2])));
						responseCount++;
						if (responseCount == preferredDesksCount)
							((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.CALCULATING_NEW_OFFER);
							
					}

					
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
			case WAITING_FOR_BID_RESPONSE:
			{
				ACLMessage msg = myAgent.receive();
				if(msg != null)
				{
					if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
					{
						((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.HAS_DESK_TAKEN); // mamy biurko huraaa
					}
					else
					{
						((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.HAS_NO_DESK_TAKEN); // Powracamy do poczatku
					}
				}
				break;
			}
			case END:
			{
				block();				
			}
			
		}    
		
	}
	
	private void makeOffer()
	{
		DataForCalculatingBidValue bidData = preparePreferredDesksData();
		
		Price bidIncrement = bidData.getBidIncrement();	
		
		int bestDeskIndex = bidData.getDeskIndexesInOrderByGains()[preferredDesksCount-1];  // indeks najlepszego biurka
		
		Price bestDeskPrice = bidData.getPreferredDeskPrices()[bestDeskIndex];				// cena najlepszego biurka		
		
		int employeeMoney = ((EmployeeAgent)myAgent).getAmountOfMoney();
		
		AID bestDeskAID = ((EmployeeAgent)myAgent).getPreferredDesksAIDs()[bestDeskIndex];
		
		
		String messageContents = "";

		Price proposedPrice = new Price(bestDeskPrice.tokens + bidIncrement.tokens,
				bestDeskPrice.epsilons + bidIncrement.epsilons);
		
		messageContents = "bid" + ":" + proposedPrice.tokens + ":" + proposedPrice.epsilons;

		if (employeeMoney >= proposedPrice.tokens)
		{
			sendMessage(bestDeskAID, messageContents, ACLMessage.PROPOSE);
			((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.WAITING_FOR_BID_RESPONSE);
		}
		else 
			((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.NOT_ENOUGH_MONEY_TO_BID_PREFERRED_DESK); // not enough money

	}
		
	private DataForCalculatingBidValue preparePreferredDesksData()
	{
		AID[] preferredDesksAIDs = ((EmployeeAgent)myAgent).getPreferredDesksAIDs();
		HashMap<AID, Price> prices = (HashMap<AID, Price>) ((EmployeeAgent)myAgent).getDesksPrices();
		
		DataForCalculatingBidValue data = new DataForCalculatingBidValue(preferredDesksAIDs, prices);
		
		return data;	
	}
	
	private void adjustPreferredDeskPrice(AID sender, Price price)
	{
		HashMap<AID, Price> prices = (HashMap<AID, Price>) ((EmployeeAgent)myAgent).getDesksPrices();
		if (!prices.containsKey(sender))
			prices.put(sender, price);
		else
			prices.replace(sender, price);
				
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
		responseCount = 0;
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