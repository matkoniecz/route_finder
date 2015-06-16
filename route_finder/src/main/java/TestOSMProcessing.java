import graph.Graph;
import osm_processing.OSMDataDownloader;

import java.io.IOException;

public class TestOSMProcessing {
    public static void main(String[] args) throws IOException {
        OSMDataDownloader test = new OSMDataDownloader(50, 20, 0.007);
        //System.out.println(test.GetData());
        Graph graph = new Graph(test.getCacheFilename());
        graph.generateLeafletHtmlView("test.html");
    }
}
