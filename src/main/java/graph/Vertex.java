package graph;

import osm_processing.Coordinate;

public class Vertex {
    public Coordinate location;

    public Vertex(Double lat, Double lon) {
        location = new Coordinate(lat, lon);
    }
}
