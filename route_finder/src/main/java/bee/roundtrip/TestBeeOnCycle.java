package bee.roundtrip;

import bee.Hive;
import graph.Graph;
import osm_processing.OSMDataDownloader;

import java.io.IOException;

public class TestBeeOnCycle {
    public static void main(String[] args) throws IOException {
        OSMDataDownloader test = new OSMDataDownloader(50.05, 19.85, 0.05);
        Graph graph = new Graph(test.getCacheFilename());
        graph.generateLeafletHtmlView("test.html");
        System.out.println("\nBegin Simulated Bee Colony algorithm\n");
        System.out.println("Finding good path on OSM data");
        Hive hive = new Hive(20, 4, 10, 6, 100, 50, new Location(test, 10.0));
        System.out.println("\nInitial random hive");
        System.out.println(hive.bestMeasureOfQuality);
        System.out.println(hive.ToString());

        hive.Solve(true);

        System.out.println("\nFinal hive");
        System.out.println(hive.bestMeasureOfQuality);
        Cycle best = (Cycle) hive.bestSolution;
        Graph merged = new Graph();
        int i = 0;
        for(Graph g: best.paths){
            merged.merge(g);
            merged.generateLeafletHtmlView(i+"test.html");
            i++;
        }
        System.out.println("\nEnd Simulated Bee Colony");
    }
}
