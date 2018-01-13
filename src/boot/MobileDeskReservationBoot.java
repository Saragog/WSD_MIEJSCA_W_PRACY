package boot;

import agents.EmployeeAgent;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 * 4      				- liczba preferencji pojedynczego pracownika
 * 100 75 50 25			- procentowe wyrazenie preferencji (max cena)
 * 4					- liczba biurek
 * 2					- liczba pracownikow
 * 1 2 3 4				- preferencje pracownika 1
 * 100					- liczba tokenow pracownika 1
 * 2 1 3 4				- preferencje pracownika 2
 * 100					- liczba tokenow praconwika 2
 */

public class MobileDeskReservationBoot {
	
	private static final String fileName = "input.txt";

	public static void main(String[] args)
	{
		Runtime runtime = Runtime.instance();
	    Profile profile = new ProfileImpl();
	    profile.setParameter(Profile.MAIN_HOST, "localhost");
	    profile.setParameter(Profile.GUI, "true");
	    ContainerController containerController = runtime.createMainContainer(profile);
	    
	    AuctionInputReader air = new AuctionInputReader("input.txt");

		int preferenceNumber = air.getPreferenceNumber();
		int deskCount = air.getDeskCount();
		int employeeCount = air.getEmployeeCount();
		int preferredDeskMaxPrices[] = air.getPreferredDeskMaxPrices();
		int employeePreferences[][] = air.getEmployeePreferences();
		int employeeTokens[] = air.getEmployeeTokens();
	    
	    Object[] employeeArgs = new Object[3];						//liczba argumentow odbieranych przez pracownika
	    Object[] deskArgs = new Object[1];
	    AID[] allDesks = new AID[deskCount];						// identyfikatory biurek 
	    AID[] preferredDesksIndices = new AID[preferenceNumber];	// identyfikatory biurek preferowanych przez pracownika	    
	    
		for(int i=0; i < deskCount; i++)
			allDesks[i] = new AID("Biurko"+(i+1),AID.ISLOCALNAME);
		
	    deskArgs[0] = allDesks;
	    
	    EmployeeAgent.setMaxDeskPrices(preferredDeskMaxPrices);
	    
	    /*-------------------------
	     * Tworzenie agentow biurek
	     * ------------------------*/
	    
	    System.out.println("DeskCount: " + deskCount + " EmplCount: " + employeeCount);
	    
	    for(int i=0; i < deskCount; ++i)
	    {
	    	System.out.println("A " + i + " ");
		    employeeArgs[0] = allDesks;
		    employeeArgs[1] = employeePreferences[i];
		    employeeArgs[2] = employeeTokens[i];
	    	
	    	AgentController deskAgentController;
	        try
	        {
	        	deskAgentController = containerController.createNewAgent("Biurko" + (i+1), "agents.DeskAgent", deskArgs);
	            deskAgentController.start();
	        }
	        catch (StaleProxyException e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    
	    /*-----------------------------
	     * Tworzenie agentow pracownikow.
	     *------------------------------*/
	    for(int i=0; i < employeeCount; i++) // TODO MAREK
	    {
	    	System.out.println("B");

	    	AgentController employeeAgentController;
	        try
	        {
	        	employeeAgentController = containerController.createNewAgent("Pracownik" + (i+1), "agents.EmployeeAgent", employeeArgs);
	            employeeAgentController.start();
	        }
	        catch (StaleProxyException e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    
	}	
}