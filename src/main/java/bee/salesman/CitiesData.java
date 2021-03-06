package bee.salesman;

import bee.Problem;
import bee.Solution;

import java.util.Random;

public class CitiesData implements Problem {
	
	public char[] cities;
	
	public CitiesData(int numberCities){
		this.cities = new char[numberCities];
		this.cities[0] = 'A';
		for(int i = 1; i < this.cities.length; ++i){
			this.cities[i] = (char)(this.cities[i-1] +1);
		}
	}
	
	public Integer Distance(char firstCity, char secondCity){
		if(firstCity < secondCity) {
			return 2 * ((int) secondCity - (int) firstCity);
		} else {
			return 3 * ((int) firstCity - (int) secondCity);
		}
	}
	
	public Integer ShortestPathLength(){
		return 2*(this.cities.length-1);
	}
	
	public long NumberOfPossiblePaths(){
		long n = this.cities.length;
		long answer = 1;
		for(int i = 1; i<= n; i++) {
			answer *= i;
		}
		return answer;
	}
	
	public String ToString(){
		String s = "";
		s += "Cities: ";
		for (int i = 0; i < this.cities.length; i++) {
			s += this.cities[i] + " ";
		}
		return s;
	}

	@Override
	public Solution getRandomSolution() {
		Random random = new Random();
		// in this case it basically takes the array of cities and swaps it around "length" times
		char[] result = this.cities.clone();

		for(int i = 0; i< result.length; i++){
			int r = random.nextInt(result.length);
			char temp = result[r];
			result[r] = result[i];
			result[i] = temp;
		}
		return new CitiesSolution(result, this);
	}
}
	
