import java.io.IOException;

public class TestRunner {
    public static void main(String[] args) throws IOException {
        OSMDataDownloader test = new OSMDataDownloader(50, 20, 0.0001);
        System.out.println(test.GetData());
    }
}
