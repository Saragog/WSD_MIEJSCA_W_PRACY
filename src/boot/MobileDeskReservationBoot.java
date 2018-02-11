package boot;

import java.util.Arrays;

import agents.EmployeeAgent;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Price;

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
	    //profile.setParameter(Profile.GUI, "true");
	    
	    ContainerController containerController = runtime.createMainContainer(profile);
	    
	    // TODO sprawdzenie dzialania generatora danych wejsciowych
	    // nazwa pliku | liczba preferencji | liczba biurek | liczba pracownikow
	    
	    // TODO narazie jest nie_singleton potem zrobic by to byl singleton i w ogole sie dowiedziec jak to sie robi
	    InputDataGenerator inputDataGenerator = new InputDataGenerator();
	    inputDataGenerator.generateInputFile("test.txt", 4, 2, 4);
	    
	    AuctionInputReader air = new AuctionInputReader(fileName);

		int preferenceNumber = air.getPreferenceNumber();
		int deskCount = air.getDeskCount();
		int employeeCount = air.getEmployeeCount();
		Price.maxEps = employeeCount;
		
		int preferredDesksMaxBidTokenPercentages[] = air.getPreferredDeskMaxBidTokenPercentages();
		int employeePreferences[][] = air.getEmployeePreferences();
		int employeeTokens[] = air.getEmployeeTokens();
	    
	    Object[] employeeArgs; 				//liczba argumentow odbieranych przez pracownika
	    Object[] deskArgs = new Object[1];
	    AID[] allDesks = new AID[deskCount];						// identyfikatory biurek 
	    AID[] preferredDesksIndices = new AID[preferenceNumber];	// identyfikatory biurek preferowanych przez pracownika	    
	    
		for(int i=0; i < deskCount; i++)
			allDesks[i] = new AID("Biurko"+(i+1),AID.ISLOCALNAME);
		
	    deskArgs[0] = allDesks;
	  
	    
	    EmployeeAgent.setPreferredDeksMaxBidTokenPercentages(preferredDesksMaxBidTokenPercentages);
	    
	    /*-------------------------
	     * Tworzenie agentow biurek
	     * ------------------------*/
	    
	    System.out.println("DeskCount: " + deskCount + " EmplCount: " + employeeCount);
	    
	    
	    for(int i=0; i < deskCount; ++i)
	    {
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
	    for(int i=0; i < employeeCount; i++)
	    {
	    	employeeArgs = new Object[3];	
	    	employeeArgs[0] = allDesks;
		    employeeArgs[1] = Arrays.stream(employeePreferences[i]).boxed().toArray(Integer[]::new);
		    employeeArgs[2] = employeeTokens[i];
	    	
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
	
	private static AID[] deducePreferredDeskAIDs(AID[] allDesks, int[] employeePreferences)
	{
		int len = employeePreferences.length;
		AID preferredDesksAIDs[] = new AID[len];
		for (int x = 0; x < len; x++)
			preferredDesksAIDs[x] = allDesks[employeePreferences[x]-1];
		return preferredDesksAIDs;
	}
}