package graph;

import osm_processing.DistanceCalculator;

import java.util.HashMap;

public class Edge {
    public Long from;
    public Long to;
    public HashMap<String, String> tags;
    public Double lengthInKilometers;

    public Edge(Long from, Long to, HashMap<String, String> tags, HashMap<Long, Vertex> nodes) {
        this.from = from;
        this.to = to;
        this.tags = tags;
        Vertex from_node = nodes.get(from);
        Vertex to_node = nodes.get(to);
        lengthInKilometers = DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(from_node.location, to_node.location);
    }

    public Integer rateWay(){
        String highwayType = tags.get("highway");
        if(highwayType.equals("cycleway")){
            return 1000;
        }
        if(highwayType.equals("footway")){
            return 10;
        }
        if(highwayType.equals("living_street")){
            return 100;
        }
        if(highwayType.equals("path")){
            return 10;
        }
        if(highwayType.equals("pedestrian")){
            return 15;
        }
        if(highwayType.equals("primary")){
            return 0;
        }
        if(highwayType.equals("primary_link")){
            return 0;
        }
        if(highwayType.equals("secondary")){
            return 0;
        }
        if(highwayType.equals("secondary_link")){
            return 0;
        }
        if(highwayType.equals("trunk")){
            return 0;
        }
        if(highwayType.equals("trunk_link")){
            return 0;
        }
        if(highwayType.equals("residential") || highwayType.equals("unclassified")){
            return 150;
        }
        if(highwayType.equals("road")){
            return 150;
        }
        if(highwayType.equals("service")){
            return 150;
        }
        if(highwayType.equals("steps")){
            return -15;
        }
        if(highwayType.equals("tertiary") || highwayType.equals("tertiary_link")){
            return 50;
        }
        if(highwayType.equals("track")){
            return 250;
        }
        throw new IllegalArgumentException();
    }
}
