package bee.roundtrip;

import bee.Solution;
import graph.Edge;
import graph.Graph;

import java.util.*;

public class Cycle implements Solution {
    List<Long> nodes;
    Location location;
    List<Graph> paths;
    public Cycle(List<Long> nodes, Location location) {
        if(nodes.size()<=2){
            throw new AssertionError("too small list ");
        }
        this.nodes = nodes;
        this.location = location;
        paths = new LinkedList<>();
        Long prev = null;
        for(Long n: nodes){
            if(prev != null){
                paths.add(location.graph.findPath(prev, n));
            }
            prev = n;
        }
        paths.add(location.graph.findPath(prev, nodes.get(0)));
    }

    @Override
    public Integer fitnessGreaterIsBetter() {
        if(paths == null){
            return Integer.MIN_VALUE;
        }
        double length = 0;
        double lengthReused = 0;
        Integer attractiveness = 0;
        HashSet<Edge> used = new LinkedHashSet<>();
        for(Graph g: paths){
            if(g == null){
                return Integer.MIN_VALUE;
            }
            for(Edge e: g.ways) {
                length += e.lengthInKilometers;
                if(used.contains(e)){
                    attractiveness -= (int) (10000 * e.lengthInKilometers);
                    lengthReused += e.lengthInKilometers;
                } else {
                    attractiveness += e.rateWay();
                    used.add(e);
                }
            }
        }
        Double delta = Math.abs(length - location.wantedLength);
        Double deltaMagnified = Math.pow(delta,3)*1000;
        Double returned = attractiveness - Math.max(0, deltaMagnified - 3000);
        int percentReused = (int) (100*length/lengthReused);
        int percentOriginal = 100 - percentReused;
        int score = returned.intValue();
        if (score>0){
            score = score*percentOriginal*percentOriginal*percentOriginal;
        }
        return score;
    }

    @Override
    public Solution getSimilarSolution() {
        if(nodes.size()<=2){
            throw new AssertionError("too small list - impossible happened");
        }
        Random random = new Random();
        Edge randEdge = location.getRandomEdge();
        Long randomNode = randEdge.from;
        List<Long> newNodes = new ArrayList<>(nodes);
        int index = random.nextInt(nodes.size());
        if(random.nextBoolean()) {
            int indexBis = random.nextInt(nodes.size());
            Long swap = newNodes.get(indexBis);
            newNodes.set(indexBis, newNodes.get(index));
            newNodes.set(index, swap);
        }else if(random.nextBoolean()) {
            Long modified = newNodes.get(index);
            List<Edge> next = location.graph.nodes.get(modified).outgoingWays;
            modified = next.get((new Random()).nextInt(next.size())).to;
            newNodes.set(index, modified);
        }else if(random.nextBoolean()) {
            newNodes.set(index, randomNode);
        }else if(random.nextBoolean() && nodes.size()>3) {
            newNodes.remove(index);
        } else {
            newNodes.add(index, randomNode);
        }
        if(newNodes.size()<=2){
            throw new AssertionError("too small list - impossible happened");
        }
        return new Cycle(newNodes, location);
    }

    @Override
    public Solution clone() {
        return new Cycle(nodes, location);
    }

    @Override
    public String ToString() {
        String returned = "";
        for(Long n: nodes){
            returned += n + " -> ";
        }
        return returned;
    }
}
