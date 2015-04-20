import graph.Graph;

import java.io.IOException;

public class TestRunner {
    public static void main(String[] args) throws IOException {
        OSMDataDownloader test = new OSMDataDownloader(50, 20, 0.002);
        System.out.println(test.GetData());
        Graph graph = new Graph(test.getCacheFilename());
        graph.generateLeafletHtmlView("test.html");
    }
}
