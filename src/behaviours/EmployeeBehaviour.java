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

	private int allDesksCount;
	private int desksCount;
	
	// private static final int EPSILON = 1; // TODO aktualnie nie wykorzystany omowic to co mowil Palka
	
	private class DataForCalculatingBidValue
	{		
		private Price bidIncrement;
		private Price[] deskPrices;
		
		private AID deskToBidAID;
		
		private Integer[] deskIndexesInOrderByGains; 
		
		public DataForCalculatingBidValue(AID[] deskAIDs, HashMap<AID, Price> mapOfDeskPrices)
		{
			// TODO tutaj duuuuuzo zmian beeedzie nie wiem ile ale duuuzo :( ...
						
			float[] allDeskMaxBidTokenParts = ((EmployeeAgent)myAgent).getAllDeskMaxBidTokenParts();
			desksCount = allDeskMaxBidTokenParts.length;

			// TODO zmienic na cos sensownego na teraz ...
			// TODO to bedzie dzialac ale to czytanie mapy i przeksztalcanie jej calej na tablice wiec dla efektywnosci przerobic pozniej
			deskPrices =  readPreferredDeskPricesFromMap(deskAIDs, mapOfDeskPrices);
			
			allDesksCount = deskAIDs.length;
			Price[] deskGains = calculateDeskGains(allDeskMaxBidTokenParts, deskPrices); // wartosci Z
			
			System.out.println(Arrays.toString(deskGains));
			
			// DO TAD PRZEROBIC BO TO ZAMIANA MAPY NA TABLICE CO JEST SLABE
			
			deskIndexesInOrderByGains = new Integer[desksCount];
			
			for (int x = 0; x < desksCount; x++)
				deskIndexesInOrderByGains[x] = x;
			
			//System.out.println("Wyznaczone wartosci Z: " + Arrays.toString(deskGains));
			
			//System.out.println("Indeksowanie stolow przed sortowaniu ich po Z: " + Arrays.toString(deskIndexesInOrderByGains));
			
			Arrays.sort(deskIndexesInOrderByGains, new Comparator<Integer>() {
				public int compare(Integer first, Integer second)
				{
					return deskGains[first].compareTo(deskGains[second]);
				}
			});
			
			
			//System.out.println("Indeksowanie stolow po sortowaniu ich po ich Z: " + Arrays.toString(deskIndexesInOrderByGains));
			
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
			
			// DO WYPISYWANIA POTEM WYRZUCIC !!! //
			
			//Price best_p =
			//Price second_p = 
			
			//System.out.println("Najlepsze biurko" + );
			
			// DO TAD WYPISYWANIE POTEM WYRZUCIC !!! //
			
			int bestDeskIndex = deskIndexesInOrderByGains[desksCount - 1], secondDeskIndex = deskIndexesInOrderByGains[desksCount - 2];
			int bidTokens = deskGains[bestDeskIndex].tokens - deskGains[secondDeskIndex].tokens;
			int bidEpsilons = deskGains[bestDeskIndex].epsilons - deskGains[secondDeskIndex].epsilons + 1;
			
			deskToBidAID = deskAIDs[bestDeskIndex];
			
			bidIncrement = new Price(bidTokens, bidEpsilons);
		}
		
		private Price[] readPreferredDeskPricesFromMap(AID[] deskAIDs, HashMap<AID, Price> mapOfDeskPrices)
		{
			Price[] preferredDeskPrices = new Price[desksCount];
			for (int index = 0; index < desksCount; index++)
				preferredDeskPrices[index] = mapOfDeskPrices.get(deskAIDs[index]);
			return preferredDeskPrices;
		}
		
		private Price[] calculateDeskGains(float[] maxDeskBidTokenPercentages, Price[] deskPrices)
		{
			int deskGainTokens, deskGainEpsilons;
			int employeeMoney = ((EmployeeAgent)myAgent).getAmountOfMoney();

			Price[] deskGains = new Price[desksCount];
			for (int deskIndex = 0; deskIndex < desksCount; deskIndex++)
			{
				deskGainTokens = (int)(maxDeskBidTokenPercentages[deskIndex] * employeeMoney) - deskPrices[deskIndex].tokens;
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
			return deskPrices;
		}
		
		public Integer[] getDeskIndexesInOrderByGains()
		{
			return deskIndexesInOrderByGains;
		}
		
		public AID getDeskToBidAID()
		{
			return deskToBidAID;
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
						priceResponseCounter--;
						if (priceResponseCounter < 1)
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
		
		int bestDeskIndex = bidData.getDeskIndexesInOrderByGains()[desksCount-1];  // indeks najlepszego biurka
		
		Price bestDeskPrice = bidData.getPreferredDeskPrices()[bestDeskIndex];				// cena najlepszego biurka		
		
		int employeeMoney = ((EmployeeAgent)myAgent).getAmountOfMoney();
		
		AID deskToBidAID = bidData.getDeskToBidAID();
		
		// Do debugowania
		
		String name = ((EmployeeAgent)myAgent).getLocalName();
		// To wywalic gdy bedzie naprawione
		
		Price proposedPrice = new Price(bestDeskPrice.tokens + bidIncrement.tokens,
				bestDeskPrice.epsilons + bidIncrement.epsilons);
		
		String messageContents = "bid" + ":" + proposedPrice.tokens + ":" + proposedPrice.epsilons;

		if (employeeMoney >= proposedPrice.tokens)
		{
			//System.out.println("Wysylam: " + name);
			sendMessage(deskToBidAID, messageContents, ACLMessage.PROPOSE);
			((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.WAITING_FOR_BID_RESPONSE);
		}
		//else 
		//	((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.NOT_ENOUGH_MONEY_TO_BID_PREFERRED_DESK); // not enough money
		
		//System.out.println("Jol, jol, Jestem pracownik: " + name);
		//System.out.println("Jestem prosty czlowiek i dla mnie w deskach podoba sie zysk z ich posiadania");
		//for (int x = 0; x < desks.length; x++)
		//System.out.println("Najbardziej podoba mi sie deska " + deskToBidAID.getLocalName() + " bo ma takie fajne atrybuty, wiec zaplace za nia " + proposedPrice.toString());
		
		//System.out.println("Koncze bidowanie pracownik: " + ((EmployeeAgent)myAgent).getLocalName());
	}
		
	private DataForCalculatingBidValue preparePreferredDesksData()
	{
		AID[] desksAIDs = ((EmployeeAgent)myAgent).getAllDesks(); // Wszystkie AIDs-y
		HashMap<AID, Price> prices = (HashMap<AID, Price>) ((EmployeeAgent)myAgent).getDesksPrices();
		
		DataForCalculatingBidValue data = new DataForCalculatingBidValue(desksAIDs, prices);
		
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
	}
	
	private void askPreferredDesksAboutPrice()
	{
		AID[] allDesks = ((EmployeeAgent)myAgent).getAllDesks();
		int desksCount = allDesks.length;
		
		String content = "priceQuestion";
		
		for (int x = 0; x < desksCount; x++)
			sendMessage(allDesks[x], content, ACLMessage.INFORM_REF);
		
		priceResponseCounter = desksCount;
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