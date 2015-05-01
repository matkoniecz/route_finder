package bee.trip;

import bee.Hive;
import bee.salesman.CitiesData;
import graph.Graph;
import osm_processing.OSMDataDownloader;

import java.io.IOException;

public class TestBeeOnPath {
    public static void main(String[] args) throws IOException {
        OSMDataDownloader test = new OSMDataDownloader(50, 20, 0.01);
        Graph graph = new Graph(test.getCacheFilename());
        graph.generateLeafletHtmlView("test.html");
        System.out.println("\nBegin Simulated Bee Colony algorithm\n");
        System.out.println("Finding good path on OSM data");
        Hive hive = new Hive(100, 20, 50, 30, 100, 100, new Location(test, 10.0));
        System.out.println("\nInitial random hive");
        //System.out.println(hive.bestMemoryMatrix);
        System.out.println(hive.bestMeasureOfQuality);
        System.out.println(hive.ToString());

        hive.Solve(true);


        System.out.println("\nFinal hive");
        //System.out.println(hive.bestMemoryMatrix);
        System.out.println(hive.bestMeasureOfQuality);
        Path best = (Path) hive.bestSolution;
        best.path.generateLeafletHtmlView("test.html");
        //System.out.println(hive.ToString());


        System.out.println("\nEnd Simulated Bee Colony");
    }
}
