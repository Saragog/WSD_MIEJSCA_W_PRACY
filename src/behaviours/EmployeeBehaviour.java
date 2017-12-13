package behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class EmployeeBehaviour extends CyclicBehaviour{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null	) 
		{
			
			System.out.println("wiadomosc: "+msg.getContent());
		
		}
		else
		{
			block();
		}        
		
	}
	
}