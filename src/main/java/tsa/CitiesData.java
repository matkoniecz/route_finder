package tsa;

public class CitiesData {
	
	public char[] cities;
	
	public CitiesData(int numberCities){
		this.cities = new char[numberCities];
		this.cities[0] = 'A';
		for(int i = 1; i < this.cities.length; ++i){
			this.cities[i] = (char)(this.cities[i-1] +1);
		}
	}
	
	public double Distance(char firstCity, char secondCity){
		if(firstCity < secondCity)
			return 1.0* ((int)secondCity - (int)firstCity);
		else
			return 1.5* ((int)firstCity - (int)secondCity);
	}
	
	public double ShortestPathLenght(){
		return 1.0*(this.cities.length-1);
	}
	
	public long NumberOfPossiblePaths(){
		long n = this.cities.length;
		long answer = 1;
		for(int i = 1; i<= n; i++)
			answer *= i;
		return answer;
	}
	
	public String ToString(){
		String s = "";
		s += "Cities: ";
		for (int i = 0; i < this.cities.length; i++)
			s += this.cities[i] + " ";
		return s;
	}
	
}
	
