package boot;

import agents.EmployeeAgent;
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
	    Object[] deskArgs = new Object[1];
	    AID[] allDesks = new AID[4];	// identyfikatory biurek 
	    AID[] preferredDesksIndices = new AID[4];	// identyfikatory biurek preferowanych przez pracownika
	    //wypelnic 
	    employeeArgs[0] = allDesks;
	    employeeArgs[1] = preferredDesksIndices;
	    	    
	    initAllDesks(allDesks);
	    deskArgs[0] = allDesks;
	    
	    /*-------------------------
	     * Ustawianie podstawowych ograniczen gornych dla preferowanych miejsc
	     * Poki co to tak na sztywno tutaj bedzie / potem gdy juz bedzie dzialac wszystko
	     * to bedzie mozna dorobic jakies czytanie z pliku by to mozna bylo uzaleznic i
	     * latwo zmieniac
	     * ------------------------*/
	    int[] maxDeskPrices = new int[4];
	    maxDeskPrices[0] = 100;
	    maxDeskPrices[1] = 75;
	    maxDeskPrices[2] = 50;
	    maxDeskPrices[3] = 25;
	    EmployeeAgent.setMaxDeskPrices(maxDeskPrices);
	    
	    /*-------------------------
	     * Tworzenie agentow biurek
	     * ------------------------*/
	    for(int i=1; i<=4; i++)
	    {
	    	AgentController deskAgentController;
	        try
	        {
	        	deskAgentController = containerController.createNewAgent("Biurko" + i, "agents.DeskAgent", deskArgs);
	            deskAgentController.start();
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
	        	initpreferredDesksIndices(i, allDesks, preferredDesksIndices); // nadawanie preferencji pracownikom
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
	 * Tablica z AID wyszstkich biurek.
	 * Tworzona jest jeszcze przed powołaniem do życia agentów.
	 * -----------------------*/
	private static void initAllDesks(AID[] allDesks) {
		for(int i=1; i<=4; i++) {
			allDesks[i-1] = new AID("Biurko"+i,AID.ISLOCALNAME);
		}
	}

	/*-------------------------
	 * Preferencje pracownikow.
	 * -----------------------*/
	private static void initpreferredDesksIndices(int option, AID[] allDesks, AID[] preferredDesksIndices) {
		switch (option) {
			case 1: 
			{
				preferredDesksIndices[0] = allDesks[0];
				preferredDesksIndices[1] = allDesks[1];
				preferredDesksIndices[2] = allDesks[2];
				preferredDesksIndices[3] = allDesks[3];
				break;
			}
			case 2:
			{
				preferredDesksIndices[0] = allDesks[1];
				preferredDesksIndices[1] = allDesks[0];
				preferredDesksIndices[2] = allDesks[2];
				preferredDesksIndices[3] = allDesks[3];
				break;
			}
			
		}
	}
	
}