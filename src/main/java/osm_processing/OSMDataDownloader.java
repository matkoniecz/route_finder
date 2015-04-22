package osm_processing;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URLEncoder;

public class OSMDataDownloader {
    private final double latitude_of_center;
    private final double longitude_of_center;
    private final double delta_in_degrees;
    public OSMDataDownloader(double latitude_of_center, double longitude_of_center, double delta_in_degrees) throws IOException {
        this.latitude_of_center = latitude_of_center;
        this.longitude_of_center = longitude_of_center;
        this.delta_in_degrees = delta_in_degrees;
        GetData();
    }

    public String getOverpassUrl() throws UnsupportedEncodingException {
        //TODO - check for crossing 180, check for negative latitudes/longitudes
        String lower_bottom_corner = (latitude_of_center-delta_in_degrees) + "," + (longitude_of_center-delta_in_degrees);
        String upper_right_corner = (latitude_of_center+delta_in_degrees) + "," + (longitude_of_center+delta_in_degrees);
        String bb = lower_bottom_corner + "," + upper_right_corner;
        return "http://overpass-api.de/api/interpreter?data=" + URLEncoder.encode("(node("+bb+");<;);out meta;", "UTF-8");
    }

    public String getCacheFilename(){
        return latitude_of_center+","+longitude_of_center+"("+delta_in_degrees+")"+".osm";
    }

    public String GetDataFromUrl() throws IOException {
        String returned;
        //copied from http://hc.apache.org/httpcomponents-client-4.4.x/quickstart.html

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(getOverpassUrl());
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(response1.getStatusLine());
            HttpEntity entity = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            returned = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
        }
        return returned;
    }

    public String GetData() throws IOException {
        String cache = getCache();
        if (cache != null) {
            return cache;
        }
        String data = GetDataFromUrl();
        saveCache(data);
        return data;
    }

    private String getCache() throws IOException {
        if(new File(getCacheFilename()).isFile()){
            try(FileInputStream inputStream = new FileInputStream(getCacheFilename())) {
                return IOUtils.toString(inputStream);
            }
        }
        return null;
    }

    private void saveCache(String data) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(getCacheFilename(), "UTF-8");
        writer.print(data);
        writer.close();
    }
}
