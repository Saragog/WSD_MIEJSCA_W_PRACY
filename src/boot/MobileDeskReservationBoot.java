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
	    Object[] employeeArgs = new Object[2];	//liczba argumentow odbieranych przez pracownika
	    AID[] allDesks = new AID[4];	// identyfikatory biurek 
	    int[] preferredDesksIndices = new int[4];	// identyfikatory biurek preferowanych przez pracownika
	    //wypelnic 
	    employeeArgs[0] = allDesks;
	    employeeArgs[1] = preferredDesksIndices;
	    
	    initpreferredDesksIndices(1, preferredDesksIndices);
	    
	    /*-------------------------
	     * Tworzenie agentow biurek
	     * ------------------------*/
	    for(int i=1; i<=4; i++)
	    {
	    	AgentController deskAgentController;
	        try
	        {
	        	deskAgentController = containerController.createNewAgent("Biurko"+i, "agents.DeskAgent", null);
	            deskAgentController.start();
	            allDesks[i-1] = new AID("Biurko"+i,AID.ISLOCALNAME);
	        }
	        catch (StaleProxyException e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    
	    /*-----------------------------
	     * Tworzenie agentow praciownikow.
	     *------------------------------*/
	    for(int i=1; i<=2; i++)
	    {
	    	AgentController employeeAgentController;
	        try
	        {
	        	initpreferredDesksIndices(i, preferredDesksIndices); // nadawanie preferencji pracownikom
	        	employeeAgentController = containerController.createNewAgent("Pracownik" + i, "agents.EmployeeAgent", employeeArgs);
	            employeeAgentController.start();
	        }
	        catch (StaleProxyException e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    
	}

	/*-------------------------
	 * Preferencje pracownikow.
	 * -----------------------*/
	private static void initpreferredDesksIndices(int option, int[] preferredDesksIndices) {
		switch (option) {
			case 1: 
			{
				preferredDesksIndices[0] = 1;
				preferredDesksIndices[1] = 2;
				preferredDesksIndices[2] = 3;
				preferredDesksIndices[3] = 4;
				break;
			}
			case 2:
			{
				preferredDesksIndices[0] = 2;
				preferredDesksIndices[1] = 1;
				preferredDesksIndices[2] = 3;
				preferredDesksIndices[3] = 4;
				break;
			}
			
		}
	}
	
}