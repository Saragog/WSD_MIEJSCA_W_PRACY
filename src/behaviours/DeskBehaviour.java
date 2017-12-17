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
		
		if (msg != null	) 
		{
			//System.out.println("wiadomosc: "+msg.getContent());
			//System.out.println("wiadomosc: "+msg.getPerformative());
			//System.out.println("wiadomosc: "+msg.getSender());
			content = msg.getContent();
			performative = msg.getPerformative();
			sender = msg.getSender();

			switch (agentState)
			{
				case FREE:
				{

					System.out.println("Obecna cena to: " + ((DeskAgent)myAgent).getCurrentPrice());
					System.out.println("Otrzymane od: " + sender);
										
					sendMessage(new AID("Pracownik1",AID.ISLOCALNAME),
							    Integer.toString(((DeskAgent)myAgent).getCurrentPrice()));
					
				
				}
				case TAKEN:
				{
					
				}

				
			};
		}
		else
		{
			block();
		}        
		
	}
	 
	private void sendMessage(AID receiver, String content)
	{
		ACLMessage messageToBeSent = new ACLMessage(ACLMessage.INFORM_REF);
		messageToBeSent.addReceiver(receiver);
		//messageToBeSent.setLanguage("jezykWSD");
		//messageToBeSent.setOntology("OntologiaPrawdy");
		messageToBeSent.setContent(content);		
		myAgent.send(messageToBeSent);
	}
}
