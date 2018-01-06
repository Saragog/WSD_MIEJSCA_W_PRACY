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
		AID sender;	
		DeskAgent myDeskAgent = ((DeskAgent)myAgent);
		DeskState agentState = myDeskAgent.getDeskState();
			
		String agentName = myAgent.getAID().getName();
		
		if (msg != null	) 
		{
			content = msg.getContent();
			performative = msg.getPerformative();
			sender = msg.getSender();
			
			
			if(performative == ACLMessage.INFORM_REF && content.equals("priceQuestion") )
			{

				System.out.println(agentName + " Obecna cena to: " + myDeskAgent.getCurrentPrice().tokens +", "+myDeskAgent.getCurrentPrice().epsilons);
									
				sendMessage(sender,
						    "price:"+Integer.toString(myDeskAgent.getCurrentPrice().tokens)+":"+Integer.toString(myDeskAgent.getCurrentPrice().epsilons),
						    ACLMessage.INFORM);
			}
			
			else
			{
			
				switch (agentState)
				{
					case FREE:
					{
						
					}
					case TAKEN:
					{
						// TODO
						// Lots of work to do in this class - for now we implement employee
					}
				};
			}
		}
		else
		{
			//block();
		}        
		
	}
	 
	private void sendMessage(AID receiver, String content, int performative)
	{
		ACLMessage messageToBeSent = new ACLMessage(performative);
		messageToBeSent.addReceiver(receiver);
		messageToBeSent.setContent(content);		
		myAgent.send(messageToBeSent);
	}
	
	private void incrementDesksTaken(DeskAgent deskAgent){
		deskAgent.setDesksTaken(deskAgent.getDesksTaken() + 1);
	}
	
	private void addEmployeeToList(AID employee, DeskAgent deskAgent){
		deskAgent.addEmployeeToList(employee);
	}
	
	private boolean shouldAuctionEnd(DeskAgent deskAgent){
		return deskAgent.getAmountOfEmployees() <= deskAgent.getDesksTaken() && deskAgent.minTimeElapsed();
	}
	
	private void endAuction(DeskAgent deskAgent){
		
		System.out.println("end auction");
		sendMessage(deskAgent.getWinningEmployee(),
			    "end auction",
			    ACLMessage.INFORM);
	}
	
}
