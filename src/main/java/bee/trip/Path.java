package bee.trip;

import bee.Solution;
import graph.Edge;
import graph.Graph;

import java.util.Random;

public class Path implements Solution {
    Long startNode;
    Long endNode;
    Location location;
    Graph path;
    public Path(Long startNode, Long endNode, Location location) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.location = location;
        path = location.graph.findPath(startNode, endNode);
    }

    @Override
    public Integer fitnessGreaterIsBetter() {
        if(path == null){
            return Integer.MIN_VALUE;
        }
        double length = 0;
        Integer attractiveness = 0;
        for(Edge e: path.ways.values()) {
            length += e.lengthInKilometers;
            attractiveness += e.rateWay();
        }
        Double delta = Math.abs(length-location.wantedLength);
        Double returned = attractiveness - Math.pow(delta,3)*1000;
        return returned.intValue();
    }

    @Override
    public Solution getSimilarSolution() {
        Path mutated = new Path(startNode, endNode, location);
        mutated.mutate();
        return mutated;
    }

    @Override
    public void mutate() {
        Random random = new Random();
        if(random.nextBoolean()){
            startNode = location.getRandomEdge().from;
        }else{
            endNode = location.getRandomEdge().to;
        }
    }

    @Override
    public Solution clone() {
        return new Path(startNode, endNode, location);
    }

    @Override
    public String ToString() {
        return startNode + " -> " + endNode;
    }
}
