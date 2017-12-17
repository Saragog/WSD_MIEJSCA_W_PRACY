package behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import agents.DeskState;
import agents.DeskAgent;

public class DeskBehaviour extends CyclicBehaviour{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void action() {
		ACLMessage msg = myAgent.receive();
		String content;
		int performative;
		DeskState agentState = ((DeskAgent)myAgent).getDeskState();
		AID sender;		
		String agentName = myAgent.getAID().getName();
		
		if (msg != null	) 
		{
			content = msg.getContent();
			performative = msg.getPerformative();
			sender = msg.getSender();
			
			switch (agentState)
			{
				case FREE:
				{

					System.out.println(agentName + "Obecna cena to: " + ((DeskAgent)myAgent).getCurrentPrice());
										
					sendMessage(sender,
							    "price:"+Integer.toString(((DeskAgent)myAgent).getCurrentPrice()),
							    ACLMessage.INFORM_REF);
				}
				case TAKEN:
				{
					// TODO
					// Lots of work to do in this class - for now we implement employee
				}
			};
		}
		else
		{
			block();
		}        
		
	}
	 
	private void sendMessage(AID receiver, String content, int performative)
	{
		ACLMessage messageToBeSent = new ACLMessage(performative);
		messageToBeSent.addReceiver(receiver);
		messageToBeSent.setContent(content);		
		myAgent.send(messageToBeSent);
	}
}
