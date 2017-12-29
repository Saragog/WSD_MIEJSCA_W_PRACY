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

public class MobileDeskReservationBoot {
	
	private static final String fileName = "input.txt";

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
	    readMaxDeskPrices(fileName, maxDeskPrices);//wczytywanie ograniczeñ górnych dla preferowanych miejsc z pliku
	    System.out.println("maxDeskPrices" + " " + maxDeskPrices[0] + " " + maxDeskPrices[1] + " " + maxDeskPrices[2] + " " + maxDeskPrices[3]);
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
	        	readPreferred(fileName, allDesks, preferredDesksIndices, i); // nadawanie preferencji pracownikom
	        	System.out.println("preferredDesksIndices" + " " + preferredDesksIndices[0] + " " + preferredDesksIndices[1] + " " + preferredDesksIndices[2] + " " + preferredDesksIndices[3]);
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
	 * Tworzona jest jeszcze przed powoÅ‚aniem do Å¼ycia agentÃ³w.
	 * -----------------------*/
	private static void initAllDesks(AID[] allDesks) {
		for(int i=1; i<=4; i++) {
			allDesks[i-1] = new AID("Biurko"+i,AID.ISLOCALNAME);
		}
	}
	
	/*-------------------------
	 * wczytywanie ograniczeñ górnych dla preferowanych miejsc z pliku
	 * -----------------------*/
	private static void readMaxDeskPrices(String fileName, int[] maxDeskPrices) {
		String line;
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			line = br.readLine();//wczytywanie ograniczen
			String[] parts = line.split(" ");
			for (int i=0; i<4; i++)
			{
				maxDeskPrices[i] = Integer.parseInt(parts[i]);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
	}
	
	/*-------------------------
	 * wczytywanie preferencji pracownikow
	 * -----------------------*/
	private static void readPreferred(String fileName, AID[] allDesks, AID[] preferredDesksIndices, int worker) {
		String line = null;
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			for (int i=0; i<(worker+1); i++)//pomijanie danych niepotrzebnych danych
			{
				line = br.readLine();
			}
			String[] parts = line.split(" ");//wczytywanie preferencji pracownika
			for (int j=0; j<4; j++)
			{
				preferredDesksIndices[j] = allDesks[Integer.parseInt(parts[j])-1];
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
	}
	
}