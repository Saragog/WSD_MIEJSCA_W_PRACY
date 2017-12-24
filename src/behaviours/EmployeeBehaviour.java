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
	
	private void makeOffer()
	{
		
		//int[] ((EmployeeAgent)myAgent).getPreferredDesksIndices();
		//int[] zValues = new int[]
		int[] preferredDesksPrices;
		/*
		for (int preferredDesk = 0; preferredDesk < 4; preferredDesk++)
		{
			preferredDesksPrices = 
		}
		*/
	}
	
	private int calculateEmployeeGainForGivenDesk(AID deskAID)
	{
		//int maxDeskPrice = 
		return 0;
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