package graph;

import osm_processing.Coordinate;
import osm_processing.DistanceCalculator;

import java.util.HashMap;

public class Edge {
    Long from;
    Long to;
    Integer attractiveness;
    Double length;

    public Edge(Long from, Long to, HashMap<Long, Vertex> nodes) {
        this.from = from;
        this.to = to;
        attractiveness = 1; //TODO - compute
        Vertex from_node = nodes.get(from);
        Vertex to_node = nodes.get(to);
        if(from_node == null || to_node == null){
            length = null;
        } else {
            length = DistanceCalculator.getDistanceBetweenCoordinates(from_node.location, to_node.location);
        }

    }
}
