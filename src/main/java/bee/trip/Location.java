package bee.trip;

import bee.Problem;
import bee.Solution;
import graph.Edge;
import graph.Graph;
import osm_processing.OSMDataDownloader;

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
        Edge eStart = getRandomEdge();
        Edge eEnd = getRandomEdge();
        Long start = eStart.from;
        Long end = eEnd.to;
        return new Path(start, end, this);
    }

    @Override
    public String ToString() {
        return null;
    }

    public Edge getRandomEdge(){
        Random r = new Random();
        return graph.ways.get(r.nextInt(graph.ways.size()));
    }
}
