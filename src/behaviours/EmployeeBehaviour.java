package behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import agents.EmployeeState;

import java.util.HashMap;

import com.sun.javafx.collections.MappingChange.Map;

import agents.*;

public class EmployeeBehaviour extends CyclicBehaviour{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int priceResponseCounter = 0;

	public void action() {
		
		
		EmployeeState agentState = ((EmployeeAgent)myAgent).getEmployeeState();
		
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
				String messageContent = msg.getContent();
				
				if (msg != null	) 
				{
					String[] contentsParts = messageContent.split(":");
					AID sender = msg.getSender();
					if (contentsParts[0] == "price")
						
					

					System.out.println("wiadomosc: "+msg.getContent());
					
				}
				else
				{
					block();
				}
				
				break;
			}
		}    
		
	}
	
	private void reakcjaNaZmianeCeny(AID sender, int price)
	{
		HashMap<AID, Integer> prices = (HashMap<AID, Integer>) ((EmployeeAgent)myAgent).getDesksPrices();
		if (prices.containsKey(sender))
			prices.put(sender, price);
		
		// zmiana licznika i ewentualnie stanu
		
		// TODO zrobic response na doslanie ceny ack ok
		
		priceResponseCounter--;
		if (priceResponseCounter < 1)
			((EmployeeAgent)myAgent).setEmployeeState(EmployeeState.CALCULATING_NEW_OFFER);
	}
	
	private void askPreferredDesksAboutPrice()
	{
		int[] preferredDesks = ((EmployeeAgent)myAgent).getPreferredDesksIndices();
		int preferredDesksCount = preferredDesks.length;
		AID[] allDesks = ((EmployeeAgent)myAgent).getAllDesks();
		
		String content = "priceQuestion";
		
		for (int x = 0; x < preferredDesksCount; x++)
			sendMessage(allDesks[preferredDesks[x]], content, ACLMessage.INFORM_REF);
		
		priceResponseCounter = preferredDesksCount;
	}
	
	private void sendMessage(AID receiver, String content, int performative)
	{
		ACLMessage messageToBeSent = new ACLMessage(performative);
		messageToBeSent.addReceiver(receiver);
		messageToBeSent.setContent(content);		
		myAgent.send(messageToBeSent);
	}
}