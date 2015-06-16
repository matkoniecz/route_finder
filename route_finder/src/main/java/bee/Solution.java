package bee;

public interface Solution {
    Integer fitnessGreaterIsBetter();
    Solution getSimilarSolution();
    Solution clone();
    String ToString();
}
