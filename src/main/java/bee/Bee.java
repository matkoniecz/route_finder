package bee;

public class Bee {
	public Status status;
	Solution solution;	//representation of solution for bees
	public Integer measureOfQuality; //representation of how good is the solution the bee's has
									// while for "komiwojazer" it's simply length of path, for us it will be slightly more complicated
	public int numberOfVisits;
	
	public Bee(Status status, Solution solution, Integer measureOfQuality, int numberOfVisits){
		this.status = status;
		this.solution = solution.clone();
		this.measureOfQuality = measureOfQuality;
		this.numberOfVisits = numberOfVisits;
	}


	public String ToString(){
		String s = "";
		s += "Status = " + this.status + "\n";
		s += "Memory = " + "\n";
		s += solution.toString();
		s += " Quality = " + this.measureOfQuality;
		s += " Number visits = " + this.numberOfVisits;
		return s;
	}

}
