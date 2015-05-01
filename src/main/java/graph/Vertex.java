package graph;

import osm_processing.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    public Coordinate location;
    public List<Edge> outgoingWays;

    public Vertex(Double lat, Double lon) {
        location = new Coordinate(lat, lon);
        outgoingWays = new ArrayList<>();
    }

}
