package bee;

public interface Solution {
    Integer fitnessGreaterIsBetter();
    Solution getSimilarSolution();
    void mutate();
    Solution clone();
    String ToString();
}
