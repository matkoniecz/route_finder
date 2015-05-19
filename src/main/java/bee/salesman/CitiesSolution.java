package bee.salesman;

import bee.Solution;

import java.util.Random;

public class CitiesSolution implements Solution {
    private final CitiesData problem;
    char[] solutionMatrix;

    public CitiesSolution(char[] solutionMatrix, CitiesData problem) {
        this.solutionMatrix = solutionMatrix;
        this.problem = problem;
    }

    @Override
    public Integer fitnessGreaterIsBetter() {
        Integer answer = 0;
        for(int i = 0; i < solutionMatrix.length - 1; i++){	// sigh... for komiwojazer it's so easy
            char c1 = solutionMatrix[i];
            char c2 = solutionMatrix[i+1];
            answer += this.problem.Distance(c1, c2);	// important to note, it says that this is the most time consuming part of the program
            			             						// (not to write but to count)
        }													// although I guess that it still depends
        return -answer;
    }

    @Override
    public Solution getSimilarSolution() {
        CitiesSolution mutated = this.clone();
        mutated.mutate();
        return mutated;
    }

    public void mutate(){
        Random random = new Random();
        int ranIndex = random.nextInt(solutionMatrix.length);
        int adjIndex = 0;

        if(ranIndex == solutionMatrix.length -1) {
            adjIndex = 0;
        } else {
            adjIndex = ranIndex + 1;
        }

        char tmp = solutionMatrix[ranIndex];			// once again a simple change between two cities
        solutionMatrix[ranIndex] = solutionMatrix[adjIndex];	// for us it will be the change of one point and following that everything will change
        // I think that there will be a need for an extra method that generates the way not only from the start but from any given point while saving what was before that
        // not that this method will differ that much from generate random way method
        // still in our case in this place we will be calling it here
        solutionMatrix[adjIndex] = tmp;
    }

    @Override
    public CitiesSolution clone() {
        return new CitiesSolution(solutionMatrix.clone(), problem);
    }

    public String ToString(){
        String s = "";
        for(int i = 0; i < this.solutionMatrix.length - 1; i++){
            s += this.solutionMatrix[i] + "->";
        }
        s += this.solutionMatrix[this.solutionMatrix.length-1] +"\n";
        return s;
    }
}
