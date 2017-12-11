package boot;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class MobileDeskReservationBoot {

	public static void main(String[] args)
	{
		Runtime runtime = Runtime.instance();
	    Profile profile = new ProfileImpl();
	    profile.setParameter(Profile.MAIN_HOST, "localhost");
	    profile.setParameter(Profile.GUI, "true");
	    ContainerController containerController = runtime.createMainContainer(profile);
  
	    for(int i=1; i<6; i++)
	    {
	    	AgentController deskAgentController;
	    	AgentController employeeAgentController;
	        try
	        {
	        	deskAgentController = containerController.createNewAgent("Miejsce do siedzenia na tylku"+i, "agents.DeskAgent", null);
	            deskAgentController.start();    
	            employeeAgentController = containerController.createNewAgent("Pracownik" + i, "agents.EmployeeAgent", null);
	            employeeAgentController.start();
	        }
	        catch (StaleProxyException e)
	        {
	        	e.printStackTrace();
	        }
	    }	
	}
}