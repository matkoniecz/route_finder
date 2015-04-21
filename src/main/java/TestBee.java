import bee.CitiesData;
import bee.Hive;

public class TestBee {

	public static void main(String[] args){
		System.out.println("\nBegin Simulated Bee Colony algorithm\n");
		System.out.println("Loading cities data for analysis");
		CitiesData citiesData = new CitiesData(20);
		System.out.println(citiesData.ToString());
		System.out.println("Number of cities = " + citiesData.cities.length);
		System.out.println("Number of possible paths = " + citiesData.NumberOfPossiblePaths());
		System.out.println("Best possible solution (shortest path) length = " + citiesData.ShortestPathLenght());
		
		Hive hive = new Hive(citiesData);
		System.out.println("\nInitial random hive");
		//System.out.println(hive.bestMemoryMatrix);
		//System.out.println(hive.bestMeasureOfQuality);
		System.out.println(hive.ToString());
		
		hive.Solve(true);
		
		
		System.out.println("\nFinal hive");
		//System.out.println(hive.bestMemoryMatrix);
		//System.out.println(hive.bestMeasureOfQuality);
		System.out.println(hive.ToString());
		
		
		System.out.println("\nEnd Simulated Bee Colony");
	}
}
