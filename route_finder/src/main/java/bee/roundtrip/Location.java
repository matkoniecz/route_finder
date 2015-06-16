package bee.roundtrip;

import bee.Problem;
import bee.Solution;
import graph.Edge;
import graph.Graph;
import osm_processing.Coordinate;
import osm_processing.DistanceCalculator;
import osm_processing.OSMDataDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Location implements Problem {
    public final Graph graph;
    public final double wantedLength;

    public Location(OSMDataDownloader dataProvider, double wantedLength) {
        graph = new Graph(dataProvider.getCacheFilename());
        this.wantedLength = wantedLength;
    }

    @Override
    public Solution getRandomSolution() {
        int count = 4;
        List<Long> initial = new ArrayList<>();
        Edge centerE = getRandomEdge();
        Long centerNode = centerE.from;
        Coordinate center = graph.nodes.get(centerNode).location;
        for(int i=0; i<count; i++){
            initial.add(getRandomNearbyNode(center, this.wantedLength));
        }
        return new Cycle(initial, this);
    }

    @Override
    public String ToString() {
        return null;
    }

    public Long getRandomNearbyNode(Coordinate center, double maxDistanceInKilometers){
        Long node;
        Double distance;
        do{
            Edge e = getRandomEdge();
            node = e.from;
            Coordinate location = graph.nodes.get(node).location;
            distance = DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(location, center);
        }while(distance > maxDistanceInKilometers);
        return node;
    }

    public Edge getRandomEdge(){
        Random r = new Random();
        return graph.ways.get(r.nextInt(graph.ways.size()));
    }
}
