package boot;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;

public class InputDataGenerator {
	
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
	
	// SINGLETON INPUT DATA GENERATOR
	// Potem przerobic ... :( Narazie nie wiem jak :(
	//private static InputDataGenerator inputDataGenerator;
	
	public class InputData{
		
		private int preferencesCount;
		private int deskCount;
		private int employeeCount;
		private int[] preferencesPercentageValues;
		private int[] employeesTokens;
		private List<List<Integer>> employeesPreferences;
		
		
		public InputData(int preferencesCount, int employeeCount, int deskCount)
		{
			this.preferencesCount = preferencesCount;
			this.employeeCount = employeeCount;
			this.deskCount = deskCount;
			
			preferencesPercentageValues = preparePercentageValues();
			employeesTokens = prepareEmployeesTokens();
			//employeesTokens = prepareEmployeesTokens(2);
			employeesPreferences = prepareEmployeesPreferences();
		}

		private List<List<Integer>> prepareEmployeesPreferences()
		{			
			List<List<Integer>> employeesPreferences = new ArrayList<List<Integer>>();
			for (int employeeIndex = 0; employeeIndex < employeeCount; employeeIndex++)
				employeesPreferences.add(randomizeEmployeePreferences());
			return employeesPreferences;
		}
		
		private List<Integer> randomizeEmployeePreferences()
		{
			int drawnNumber;
			List<Integer> employeePreferences = new ArrayList<Integer>();
			Random generator = new Random();
			for (int preferenceIndex = 0; preferenceIndex < preferencesCount;)
			{
				drawnNumber = generator.nextInt(deskCount) + 1;
				if (!employeePreferences.contains(drawnNumber))
				{
					employeePreferences.add(drawnNumber);
					++preferenceIndex;
				}
			}
			return employeePreferences;
		}
		
		private int[] preparePercentageValues()
		{
			int[] percentageValues = new int[preferencesCount];
			for (int index = 0, step = 100 / preferencesCount, partials = 0;
					index < preferencesCount; index++, partials += 100 % preferencesCount)
				percentageValues[index] = (index + 1) * step + partials / preferencesCount;
			return reverseArray(percentageValues);// .reverse(percentageValues);
		}
		
		private int[] reverseArray(int[] values)
		{
			int valuesLength = values.length, loopsNumber = valuesLength / 2, temp;
			for (int index = 0; index < loopsNumber; ++index)
			{
				temp = values[index];
				values[index] = values[valuesLength - 1 - index];
				values[valuesLength - 1 - index] = temp;
			}
			return values;
		}
		
		private int[] prepareEmployeesTokens()
		{
			int[] percentageValues = new int[preferencesCount];
			for (int index = 0; index < preferencesCount; index++)
				percentageValues[index] = 100;
			return percentageValues;
		}
		
		private int[] prepareEmployeesTokens(int employeeImportanceLevelsCount)
		{
			int[] percentageValues = new int[preferencesCount];
			float moneyGiven = (float) 0.0, moneyProgress = (float) (100.0 / (preferencesCount-1.0));
			for (int index = 0; index < preferencesCount; index++, moneyGiven += moneyProgress)
				percentageValues[index] = (int)moneyGiven;
			return percentageValues;
		}
		
		public int getPreferencesCount() {
			return preferencesCount;
		}


		public int[] getPreferencesPercentageValues() {
			return preferencesPercentageValues;
		}


		public int getDeskCount() {
			return deskCount;
		}


		public int getEmployeeCount() {
			return employeeCount;
		}


		public int[] getEmployeesTokens() {
			return employeesTokens;
		}


		public List<List<Integer>> getEmployeesPreferences() {
			return employeesPreferences;
		}
		
		
	}
	
	public void generateInputFile(String fileName, int preferencesCount, int employeeCount, int deskCount)
	{
		//if (InputDataGenerator.inputDataGenerator == null) inputDataGenerator = new InputDataGenerator();
		
		if (checkIfArgumentValuesOk(fileName, preferencesCount, employeeCount, deskCount))
		{
			System.out.println("Dane poprawne do generowania pliku wejsciowego1");
			InputData inputData = new InputData(preferencesCount, employeeCount, deskCount);
			System.out.println("Dane poprawne do generowania pliku wejsciowego2");

			saveInputDataToFile(fileName, inputData);
			System.out.println("Dane poprawne do generowania pliku wejsciowego3");

		}
	}
	
	private boolean checkIfArgumentValuesOk(String fileName, int preferencesCount, int employeeCount, int deskCount)
	{
		if (fileName.isEmpty())
			System.out.println("Brak nazwy pliku do zapisu generowanych danych!!!");
		else if (preferencesCount < 0 || employeeCount <= 0 || deskCount <= 0)
			System.out.println("Nie da sie stworzyc danych wejsciowych dla zadanych wartosci!!!: " + "\n" +
								"Preferences: " + preferencesCount +  "\n" +
								"EmployeeCount: " + employeeCount + "\n" +
								"DeskCount: " + deskCount);
		else if (employeeCount > deskCount)
		{
			System.out.println("Bledna liczebnosc pracownikow i biurek, zakladamy, ze biurek jest co najmniej tyle co pracownikow!!!");
		}
		else return true;
		return false;
	}
	
	private void saveInputDataToFile(String fileName, InputData inputData)
	{
		try(FileWriter fr = new FileWriter(fileName))
		{
			writeContentsToFile(fr, inputData);
		}
		catch(IOException ex)
		{
			System.out.println("Wystapil blad przy zapisie do pliku: " + fileName);
			return;
		}
		System.out.println("Poprawnie stworzono nowy plik z danymi wejsciowymi: " + fileName);
	}
	
	private void writeContentsToFile(FileWriter fr, InputData inputData) throws IOException
	{
		String newLine = System.lineSeparator(), arrayAsString;
		fr.write(String.valueOf(inputData.getPreferencesCount()) + newLine);
		arrayAsString = Arrays.toString(inputData.getPreferencesPercentageValues());
		fr.write(arrayAsString.substring(1, arrayAsString.length() - 1) + newLine);
		fr.write(String.valueOf(inputData.getDeskCount()) + newLine);
		int employeeCount = inputData.getEmployeeCount();
		List<List<Integer>> employeesPreferences = inputData.getEmployeesPreferences();
		int[] employeesTokens = inputData.getEmployeesTokens();
		fr.write(String.valueOf(employeeCount));
		for(int employeeIndex = 0; employeeIndex < employeeCount; employeeIndex++)
		{
			arrayAsString = Arrays.toString(employeesPreferences.get(employeeIndex).toArray());
			fr.write(newLine + arrayAsString.substring(1, arrayAsString.length() - 1));
			fr.write(newLine + String.valueOf(employeesTokens[employeeIndex]));
		}
	}
	
}
