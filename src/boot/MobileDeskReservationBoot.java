package boot;

import jade.core.AID;
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
	    Object[] employeeArgs = new Object[2];
	    AID[] allDesks = new AID[2];
	    int[] preferredDesksIndices = new int[4];
	    //wypelnic 
	    employeeArgs[0] = allDesks;
	    employeeArgs[1] = preferredDesksIndices;
	    
  
	    for(int i=1; i<2; i++)
	    {
	    	AgentController deskAgentController;
	    	AgentController employeeAgentController;
	        try
	        {
	        	deskAgentController = containerController.createNewAgent("Biurko"+i, "agents.DeskAgent", null);
	            deskAgentController.start();    
	            employeeAgentController = containerController.createNewAgent("Pracownik" + i, "agents.EmployeeAgent", employeeArgs);
	            employeeAgentController.start();
	        }
	        catch (StaleProxyException e)
	        {
	        	e.printStackTrace();
	        }
	    }	
	}
}