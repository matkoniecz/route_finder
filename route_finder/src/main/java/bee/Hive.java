package bee;

import java.util.Random;

public class Hive {

	static Random random;
	
	public Problem problem;			        // problem that is being solved
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
	public int bestSolutionCycle = 0;
											//everything public for now
	
	public double probPersuasion = 0.9;		// probability that inactive bee will use better solution presented during DoWaggleDance
											// generally though this is another value to play around with to find a better solution
											// if value is increased new solution can be found quicker
											// at risk of converging to non-optimal solution though
	public double probMistake = 0.01;		// something similiar - another value to play around with and see what happens
	
	public Bee[] bees;
	public Solution bestSolution;			// bestSolution solution that "super bee" brought
	public Integer bestMeasureOfQuality;		// a value to compare with
	public int[] indexesOfInactiveBees;		// makes it easier to check which bees are "slacking off" //are inactive 
											// [3,4,7] - means 3,4,7 are inactive
											// it's not that [0,1,0,1,0] - 1 3 5 are inactive
											// in the end though both would work, right now first is implemented
	
	
	public Hive(Problem problem){
		this(100, 20, 50, 30, 100, 3460, problem);
	}
	
	public Hive(int totalNumberBees, int numberInactive, int numberActive, int numberScout,
			int maxNumberVisits, int maxNumberCycles, Problem problem){
		random = new Random();
		
		this.totalNumberBees = totalNumberBees;
		this.numberInactive = numberInactive;
		this.numberActive = numberActive;
		this.numberScout = numberScout;
		this.maxNumberVisits = maxNumberVisits;
		this.maxNumberCycles = maxNumberCycles;

		this.problem = problem;

		this.bees = new Bee[totalNumberBees];
		this.bestSolution = problem.getRandomSolution();
		this.bestMeasureOfQuality = this.bestSolution.fitnessGreaterIsBetter();

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

			Solution randomSolution = problem.getRandomSolution();
			Integer mq = randomSolution.fitnessGreaterIsBetter();
			int numberOfVisits = 0;

			bees[i] = new Bee(currStatus, randomSolution, mq, numberOfVisits);

			if(bees[i].measureOfQuality > bestMeasureOfQuality){
				this.bestSolution = bees[i].solution.clone();
				this.bestMeasureOfQuality = bees[i].measureOfQuality;
			}
		}
	}
	
	public String ToString(){
		String s = "";
		s += "Solution: ";
		s += bestSolution.ToString();
		s += "\nQuality of solution: " + this.bestMeasureOfQuality;
		return s;
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
					ProcessActiveBee(i,cycle);
				} else if (this.bees[i].status == Status.SCOUT) {
					ProcessScoutBee(i,cycle);
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
	private void ProcessActiveBee(int i,int cycle){
		Solution neighborSolution = bees[i].solution.getSimilarSolution();
		Integer neighborQuality = neighborSolution.fitnessGreaterIsBetter();
		double prob = random.nextDouble();
		boolean memoryWasUpdated = false;
		boolean numberOfVisitsOverLimit = false;
		
		if(neighborQuality > bees[i].measureOfQuality) {	// found better neighbor
			if(prob < probMistake) {	//mistake
				++bees[i].numberOfVisits;
				if(bees[i].numberOfVisits > maxNumberVisits)
					numberOfVisitsOverLimit = true;
			}else{	// no mistake
				bees[i].solution = neighborSolution.clone();
				bees[i].measureOfQuality = neighborQuality;
				bees[i].numberOfVisits = 0;
				memoryWasUpdated = true;
			}	
		}
		else{ // did not find better neighbor
			if(prob < probMistake){ //mistake
				bees[i].solution = neighborSolution.clone();
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
			if(bees[i].measureOfQuality > this.bestMeasureOfQuality){
				this.bestSolution = bees[i].solution.clone();
				this.bestMeasureOfQuality = bees[i].measureOfQuality;
				this.bestSolutionCycle = cycle+1;
			}
			DoWaggleDance(i); //what??
		}
		else{
			return;
		}
	}
	
	public void ProcessScoutBee(int i,int cycle){		// I am not sure whatever scout actually does anything useful but whatever
		Solution randomFoodSource = problem.getRandomSolution();
		Integer randomFoodSourceQuality = randomFoodSource.fitnessGreaterIsBetter();
		if(randomFoodSourceQuality > bees[i].measureOfQuality){
			bees[i].solution = randomFoodSource.clone();
			bees[i].measureOfQuality = randomFoodSourceQuality;
			if(bees[i].measureOfQuality > bestMeasureOfQuality){
				this.bestSolution = bees[i].solution.clone();
				this.bestMeasureOfQuality = bees[i].measureOfQuality;
				this.bestSolutionCycle = cycle+1;
			}
			DoWaggleDance(i);
		}
		
	}
	
	private void DoWaggleDance(int i){		// simulates a process of bee returning to the hive
											// it conveys information to inactive bees in the hive
		for(int j = 0; j < numberInactive; j++){
			int b = indexesOfInactiveBees[j];
			if (bees[i].measureOfQuality > bees[b].measureOfQuality){
				double p = random.nextDouble();
				if(this.probPersuasion > p){
					bees[b].solution = bees[i].solution.clone();
					bees[b].measureOfQuality = bees[i].measureOfQuality;
				}
			}
		}
		
	}
	
	
	
	
}
