package boot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

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

public class AuctionInputReader
{
	private int preferenceNumber;
	private int deskCount;
	private int employeeCount;
	private int preferredDeskMaxBidTokenPercentages[];
	private int employeePreferences[][];
	private int employeeTokens[];

	AuctionInputReader(String pathToInputFile)
	{
		try {
			FileReader fr = new FileReader(pathToInputFile);
			BufferedReader br = new BufferedReader(fr);
			preferenceNumber = Integer.parseInt(br.readLine());
			preferredDeskMaxBidTokenPercentages = parseIntArray(br.readLine().split(" "));
			deskCount = Integer.parseInt(br.readLine());
			employeeCount = Integer.parseInt(br.readLine());
			employeePreferences = new int[employeeCount][];
			employeeTokens = new int[employeeCount];
			for (int employeeInputIndex = 0; employeeInputIndex < employeeCount; employeeInputIndex++)
			{
				employeePreferences[employeeInputIndex] = parseIntArray(br.readLine().split(" "));
				employeeTokens[employeeInputIndex] = Integer.parseInt(br.readLine());
			}
		}
		catch (IOException e)
		{
			// Default input data for algorythm
			
			preferenceNumber = 4;
			deskCount = 4;
			employeeCount = 2;
			preferredDeskMaxBidTokenPercentages = new int[]{100, 75, 50, 25};
			employeePreferences = new int[][] {{1, 2, 3, 4}, {2, 1, 3, 4}};
			employeeTokens = new int[] {100, 100};
			
			System.out.println("______________________________________________________________");
			System.out.println("There was a problem reading input data from specified file !!!");
			System.out.println("Default input values were used !!!");
			System.out.println("______________________________________________________________");
		}		
	}
	
	static int[] parseIntArray(String[] arr)
	{
		return Stream.of(arr).mapToInt(Integer::parseInt).toArray();
	}
	
	public int getPreferenceNumber() {
		return preferenceNumber;
	}

	public int getDeskCount() {
		return deskCount;
	}

	public int getEmployeeCount() {
		return employeeCount;
	}

	public int[] getPreferredDeskMaxBidTokenPercentages() {
		return preferredDeskMaxBidTokenPercentages;
	}

	public int[][] getEmployeePreferences() {
		return employeePreferences;
	}
	
	public int[] getEmployeeTokens() {
		return employeeTokens;
	}
}
