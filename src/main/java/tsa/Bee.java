package tsa;

public class Bee {
	public int status;		// 0 - inactive, 1 - active, 2 - scout  // TODO Change it to enumeration value later
	public char[] memoryMatrix;	//representation of solution for bees
	public double measureOfQuality; //representation of how good is the solution the bee's has
									// while for "komiwojazer" it's simply length of path, for us it will be slightly more complicated
	public int numberOfVisits;
	
	public Bee(int status, char[] memoryMatrix, double measureOfQuality, int numberOfVisits){
		this.status = status;
		this.memoryMatrix = new char[memoryMatrix.length];
		this.memoryMatrix = memoryMatrix.clone();
		this.measureOfQuality = measureOfQuality;
		this.numberOfVisits = numberOfVisits;
	}
	
	public String ToString(){
		String s = "";
		s += "Status = " + this.status + "\n";
		s += "Memory = " + "\n";
		for(int i = 0; i < this.memoryMatrix.length - 1; i++){
			s += this.memoryMatrix[i] + "->";
		}
		s += this.memoryMatrix[this.memoryMatrix.length-1] +"\n";
		s += " Quality = " + this.measureOfQuality;
		s += " Number visits = " + this.numberOfVisits;
		return s;
	}
	
}
