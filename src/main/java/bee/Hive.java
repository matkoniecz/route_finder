package bee;

import java.util.Random;

public class Hive {

	static Random random;
	
	public CitiesData citiesData;			// problem that is being solved
											// ??optimal values are active 75%, inactive 10%, scout 15%??
	int totalNumberBees = 100; 				// each bee present solution		
	int numberInactive = 20;				// so in theory the more bees the better solution we get
	int numberActive = 50;					// but also to more bees the longer it will take for program to finish	
	int numberScout = 30;
	
	int maxNumberVisits = 100;				// threshold value (wartosc progowa) which prevents the bee from having the same solution for too long
											// in solve method if a bee does not find a neighbor food source with better quality, the bee's numberOfVisits will increase
											// once that value reaches max the bees goes into inactive state
											// there is no need for it to fly anymore as it can't find any better solution
	int maxNumberCycles = 3460;
											//everything public for now
	
	public double probPersuasion = 0.9;		// probability that inactive bee will use better solution presented during DoWaggleDance
											// generally though this is another value to play around with to find a better solution
											// if value is increased new solution can be found quicker
											// at risk of converging to non-optimal solution though
	public double probMistake = 0.01;		// something similiar - another value to play around with and see what happens
	
	public Bee[] bees;
	public char[] bestMemoryMatrix;			// best solution that "super bee" brought
	public double bestMeasureOfQuality;		// a value to compare with
	public int[] indexesOfInactiveBees;		// makes it easier to check which bees are "slacking off" //are inactive 
											// [3,4,7] - means 3,4,7 are inactive
											// it's not that [0,1,0,1,0] - 1 3 5 are inactive
											// in the end though both would work, right now first is implemented
	
	
	public Hive(CitiesData citiesData){
		random = new Random();
		
		this.citiesData = citiesData;
		
		this.bees = new Bee[totalNumberBees];
		this.bestMemoryMatrix = GenerateRandomMemeoryMatrix();
		this.bestMeasureOfQuality = MeasureOfQuality(this.bestMemoryMatrix);
		
		this.indexesOfInactiveBees = new int[numberInactive];
		
		for(int i = 0; i < totalNumberBees; i++){
			Status currStatus;
			if(i < numberInactive){			// on init all "numberInactive" first bees getting inactive status
				currStatus = Status.INACTIVE;
				indexesOfInactiveBees[i] = i;  // inactive
			}
			else if (i < numberInactive + numberScout) {
				currStatus = Status.SCOUT;
			} else {
				currStatus = Status.ACTIVE;
			}
			
			char[] randomMemoryMatrix = GenerateRandomMemeoryMatrix();
			double mq = MeasureOfQuality(randomMemoryMatrix);
			int numberOfVisits = 0;
			
			bees[i] = new Bee(currStatus, randomMemoryMatrix, mq, numberOfVisits);
			
			if(bees[i].measureOfQuality < bestMeasureOfQuality){
				this.bestMemoryMatrix = bees[i].memoryMatrix.clone();
				this.bestMeasureOfQuality = bees[i].measureOfQuality;
			}
		}
	}	
	
	public Hive(int totalNumberBees, int numberInactive, int numberActive, int numberScout,
			int maxNumberVisits, int maxNumberCycles, CitiesData citiesData){
		random = new Random();
		
		this.totalNumberBees = totalNumberBees;
		this.numberInactive = numberInactive;
		this.numberActive = numberActive;
		this.numberScout = numberScout;
		this.maxNumberVisits = maxNumberVisits;
		this.maxNumberCycles = maxNumberCycles;

		this.citiesData = citiesData;
		
		this.bees = new Bee[totalNumberBees];
		this.bestMemoryMatrix = GenerateRandomMemeoryMatrix();
		this.bestMeasureOfQuality = MeasureOfQuality(this.bestMemoryMatrix);
		
		this.indexesOfInactiveBees = new int[numberInactive];
		
		for(int i = 0; i < totalNumberBees; i++){
			Status currStatus;
			if(i < numberInactive){			// on init all "numberInactive" first bees getting inactive status
				currStatus = Status.INACTIVE;
				indexesOfInactiveBees[i] = i;  // inactive
			}
			else if (i < numberInactive + numberScout) {
				currStatus = Status.SCOUT;
			} else {
				currStatus = Status.ACTIVE;
			}
			
			char[] randomMemoryMatrix = GenerateRandomMemeoryMatrix();
			double mq = MeasureOfQuality(randomMemoryMatrix);
			int numberOfVisits = 0;
			
			bees[i] = new Bee(currStatus, randomMemoryMatrix, mq, numberOfVisits);
			
			if(bees[i].measureOfQuality < bestMeasureOfQuality){
				this.bestMemoryMatrix = bees[i].memoryMatrix.clone();
				this.bestMeasureOfQuality = bees[i].measureOfQuality;
			}
		}
	}
	
	public String ToString(){
		String s = "";
		s += "Path: ";
		for(int i = 0; i < this.bestMemoryMatrix.length - 1; i++)
			s += this.bestMemoryMatrix[i] + "->";
		s += this.bestMemoryMatrix[this.bestMemoryMatrix.length-1];
		s += "\nQuality of solution: " + this.bestMeasureOfQuality;
		return s;
	}	
	
	// three important functions for SBC, this to change
	public char[] GenerateRandomMemeoryMatrix() {	// generate random solution
													// in this case it basically takes the array of cities and swaps it around "length" times
													// we naturally need a different way
		char[] result = this.citiesData.cities.clone();
		
		for(int i = 0; i< result.length; i++){
			int r = random.nextInt(result.length);
			char temp = result[r];
			result[r] = result[i];
			result[i] = temp;
		}
		return result;		
	}
	
	public char[] GenerateNeighborMemoryMatrix(char[] memoryMatrix){	// generate a solution based on neighbor solution
		char[] result = memoryMatrix.clone();
		
		int ranIndex = random.nextInt(result.length);
		int adjIndex = 0;
		
		if(ranIndex == result.length -1) {
			adjIndex = 0;
		} else {
			adjIndex = ranIndex + 1;
		}
		
		char tmp = result[ranIndex];			// once again a simple change between two cities
		result[ranIndex] = result[adjIndex];	// for us it will be the change of one point and following that everything will change
												// I think that there will be a need for an extra method that generates the way not only from the start but from any given point while saving what was before that
												// not that this method will differ that much from generate random way method
												// still in our case in this place we will be calling it here
		result[adjIndex] = tmp;
		
		return result;
	}
	
	public double MeasureOfQuality(char[] memoryMatrix){	// compete measure of quality, "fajnosc"
		double answer = 0.0;
		for(int i = 0; i < memoryMatrix.length - 1; i++){	// sigh... for komiwojazer it's so easy
			char c1 = memoryMatrix[i];						
			char c2 = memoryMatrix[i+1];
			double d = this.citiesData.Distance(c1, c2);	// important to note, it says that this is the most time consuming part of the program
			answer += d;									// (not to write but to count)
		}													// although I guess that it still depends
		return answer;
	}
	
	public void Solve(boolean doProgressBar){		// simulates the behavior of bees to solve the problem
		boolean pb = doProgressBar;
		int numberOfSymbolsToPrint = 10;
		int increment = this.maxNumberCycles / numberOfSymbolsToPrint;
		if (pb){
			System.out.println("\nEntering SBC algorithm main processing loop");
			System.out.println("Progress:|==========|");
			System.out.print("          ");
		}
		int cycle = 0;
		
		while (cycle < this.maxNumberCycles){
			for(int i = 0; i<totalNumberBees; i++) {
				if (this.bees[i].status == Status.ACTIVE) {
					ProcessActiveBee(i);
				} else if (this.bees[i].status == Status.SCOUT) {
					ProcessScoutBee(i);
				} else if (this.bees[i].status == Status.INACTIVE) {
					//do nothing
				}
			}
			++ cycle;
			
			if(pb && cycle%increment == 0) {
				System.out.print("^");
			}
		}
		if(pb) {
			System.out.println("");
		}
	}
	
	//Three helper methods for solve
	//THe first one is most complex, or at least it says it should be.
	private void ProcessActiveBee(int i){
		char[] neighbor = GenerateNeighborMemoryMatrix(bees[i].memoryMatrix);
		double neighborQuality = MeasureOfQuality(neighbor);
		double prob = random.nextDouble();
		boolean memoryWasUpdated = false;
		boolean numberOfVisitsOverLimit = false;
		
		if(neighborQuality < bees[i].measureOfQuality) {	// found better neighbor
			if(prob < probMistake) {	//mistake
				++bees[i].numberOfVisits;
				if(bees[i].numberOfVisits > maxNumberVisits)
					numberOfVisitsOverLimit = true;
			}else{	// no mistake
				bees[i].memoryMatrix = neighbor.clone();
				bees[i].measureOfQuality = neighborQuality;
				bees[i].numberOfVisits = 0;
				memoryWasUpdated = true;
			}	
		}
		else{ // did not find better neighbor
			if(prob < probMistake){ //mistake
				bees[i].memoryMatrix = neighbor.clone();
				bees[i].measureOfQuality = neighborQuality;
				bees[i].numberOfVisits = 0;
				memoryWasUpdated = true;
			}
			else{		//no mistake
				++bees[i].numberOfVisits;
				if(bees[i].numberOfVisits > maxNumberVisits)
					numberOfVisitsOverLimit = true;
			}
		}
		
		if(numberOfVisitsOverLimit){
			bees[i].status = Status.INACTIVE;
			bees[i].numberOfVisits = 0;
			int x = random.nextInt(numberInactive);
			bees[indexesOfInactiveBees[x]].status = Status.ACTIVE;
			indexesOfInactiveBees[x] = i;
		}
		else if (memoryWasUpdated){
			if(bees[i].measureOfQuality < this.bestMeasureOfQuality){		//this kind of doesn't make sense
				this.bestMemoryMatrix = bees[i].memoryMatrix.clone();		// or no wait, it does make sense because the lower the better
				this.bestMeasureOfQuality = bees[i].measureOfQuality;
			}
			DoWaggleDance(i); //what??
		}
		else{
			return;
		}
	}
	
	public void ProcessScoutBee(int i){		// I am not sure whatever scout actually does anything useful but whatever
		char[] randomFoodSource = GenerateRandomMemeoryMatrix();
		double randomFoodSourceQuality = MeasureOfQuality(randomFoodSource);
		if(randomFoodSourceQuality < bees[i].measureOfQuality){
			bees[i].memoryMatrix = randomFoodSource.clone();
			bees[i].measureOfQuality = randomFoodSourceQuality;
			if(bees[i].measureOfQuality < bestMeasureOfQuality){
				this.bestMemoryMatrix = bees[i].memoryMatrix.clone();
				this.bestMeasureOfQuality = bees[i].measureOfQuality;
			}
			DoWaggleDance(i);
		}
		
	}
	
	private void DoWaggleDance(int i){		// simulates a process of bee returning to the hive
											// it conveys information to inactive bees in the hive
		for(int j = 0; j < numberInactive; j++){
			int b = indexesOfInactiveBees[j];
			if (bees[i].measureOfQuality < bees[b].measureOfQuality){
				double p = random.nextDouble();
				if(this.probPersuasion > p){
					bees[b].memoryMatrix = bees[i].memoryMatrix.clone();
					bees[b].measureOfQuality = bees[i].measureOfQuality;
				}
			}
		}
		
	}
	
	
	
	
}
