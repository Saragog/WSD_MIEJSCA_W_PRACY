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
	private int preferredDeskMaxPrices[];
	private int employeePreferences[][];
	private int employeeTokens[];

	AuctionInputReader(String pathToInputFile)
	{
		try {
			FileReader fr = new FileReader(pathToInputFile);
			BufferedReader br = new BufferedReader(fr);
			preferenceNumber = Integer.parseInt(br.readLine());
			preferredDeskMaxPrices = parseIntArray(br.readLine().split(" "));
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
			// TODO ustalenie domyslnych danych
			e.printStackTrace();
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

	public int[] getPreferredDeskMaxPrices() {
		return preferredDeskMaxPrices;
	}

	public int[][] getEmployeePreferences() {
		return employeePreferences;
	}
	
	public int[] getEmployeeTokens() {
		return employeeTokens;
	}
}
