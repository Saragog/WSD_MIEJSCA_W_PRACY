package behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import agents.EmployeeState;
import agents.*;

public class EmployeeBehaviour extends CyclicBehaviour{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void action() {
		ACLMessage msg = myAgent.receive();
		
		EmployeeState agentState = ((EmployeeAgent)myAgent).getEmployeeState();
		
		switch(agentState)
		{
			case HAS_NO_DESK_TAKEN:
			{
				
			}
			case HAS_DESK_TAKEN:
			{
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
		
	}
	
}