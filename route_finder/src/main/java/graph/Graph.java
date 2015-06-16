package graph;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;

public class Graph {
    public HashMap<Long, Vertex> nodes;
    public List<Edge> ways;
    public Graph(){
        nodes = new HashMap<>();
        ways = new ArrayList<>();
    }

    public Graph findPath(Long nodeA, Long nodeB){
        class SearchEntry implements Comparable{
            Long nodeId;
            double distance;

            public SearchEntry(Long nodeId, double distance) {
                this.nodeId = nodeId;
                this.distance = distance;
            }

            @Override
            public int compareTo(Object o) {
                if(!(o instanceof SearchEntry)){
                    throw new IllegalArgumentException();
                }
                SearchEntry alien = (SearchEntry) o;
                if(distance < alien.distance){
                    return -1;
                }
                if(distance == alien.distance){
                    return 0;
                }
                return 1;
            }
        }
        HashMap<Long, Double> distance =  new HashMap<>();
        HashMap<Long, Edge> prev =  new HashMap<>();
        PriorityQueue<SearchEntry> potential = new PriorityQueue<>();
        distance.put(nodeA, 0.0);
        prev.put(nodeA, null);
        potential.add(new SearchEntry(nodeA, 0));
        while(potential.size()>0){
            SearchEntry candidate = potential.peek();
            potential.remove(candidate);
            Vertex node = nodes.get(candidate.nodeId);
            for(Edge e: node.outgoingWays){
                boolean update = false;
                double newPotentialLength = candidate.distance + e.lengthInKilometers;
                if(!distance.containsKey(e.to)){
                    update = true;
                } else {
                    if(distance.get(e.to)>newPotentialLength){
                        update = true;
                    }
                }
                if(update){
                    distance.put(e.to, newPotentialLength);
                    prev.put(e.to, e);
                    potential.add(new SearchEntry(e.to, newPotentialLength));
                }
            }
        }
        if(!distance.containsKey(nodeB)){
            //System.out.println("failed to find path: " + nodeA + " to " + nodeB);
            return null;
        }
        Graph returned = new Graph();
        Long node = nodeB;
        Edge toNode;
        while(true){
            toNode = prev.get(node);
            if(toNode == null){
                if(!node.equals(nodeA)){
                    throw new AssertionError();
                }
                break;
            }
            returned.ways.add(toNode);
            returned.nodes.put(node, this.nodes.get(node));
            node = toNode.from;
        }
        returned.nodes.put(node, this.nodes.get(node));
        return returned;
    }

    public Graph(String pathToOSMXmlFile){
        this();

        List<Edge> potentialWays = new ArrayList<>();
        HashMap<String, String> tags = new HashMap<>();
        Long previous_node = null;
        Long id;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(pathToOSMXmlFile));
            while(xmlEventReader.hasNext()){
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()){
                    StartElement startElement = xmlEvent.asStartElement();
                    String type = startElement.getName().getLocalPart();
                    switch (type) {
                        case "node": {
                            //System.out.println(type);
                            Double lat = new Double(startElement.getAttributeByName(new QName("lat")).getValue());
                            Double lon = new Double(startElement.getAttributeByName(new QName("lon")).getValue());
                            id = new Long(startElement.getAttributeByName(new QName("id")).getValue());
                            Vertex node = new Vertex(lat, lon);
                            nodes.put(id, node);
                            break;
                        }
                        case "way":
                            List<String> roads = Arrays.asList("cycleway", "footway", "living_street", "path",
                                    "pedestrian", "primary", "primary_link", "residential", "road", "secondary",
                                    "secondary_link", "service", "steps", "tertiary", "tertiary_link", "track", "trunk",
                                    "trunk_link", "unclassified");
                            //new way, process old one
                            if(potentialWays.size()>0){
                                if(tags.containsKey("highway")){
                                    if(roads.contains(tags.get("highway"))){
                                        ways.addAll(potentialWays);
                                        for(Edge way: potentialWays){
                                            nodes.get(way.from).outgoingWays.add(way);
                                        }
                                    }
                                }
                            }
                            potentialWays = new ArrayList<>();
                            tags = new HashMap<>();

                            //id = new Long(startElement.getAttributeByName(new QName("id")).getValue());
                            previous_node = null;
                            break;
                        case "nd": {
                            Long node_id = new Long(startElement.getAttributeByName(new QName("ref")).getValue());
                            if (previous_node != null) {
                                potentialWays.add(new Edge(previous_node, node_id, tags, nodes));
                                potentialWays.add(new Edge(node_id, previous_node, tags, nodes));
                            }
                            previous_node = node_id;
                            break;
                        }
                        case "tag":
                            String k = startElement.getAttributeByName(new QName("k")).getValue();
                            String v = startElement.getAttributeByName(new QName("v")).getValue();
                            //System.out.println(k + " " + v);
                            tags.put(k, v);
                            break;
                    }
                }
            }

        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void generateLeafletHtmlView(String filename) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        Double sumLon = 0.0;
        Double sumLat = 0.0;
        Integer summed = 0;
        for(Edge e: ways) {
            Double from_lat = nodes.get(e.from).location.latitude;
            Double from_lon = nodes.get(e.from).location.longitude;
            Double to_lat = nodes.get(e.to).location.latitude;
            Double to_lon = nodes.get(e.to).location.longitude;
            sumLon += from_lon;
            sumLon += to_lon;
            sumLat += from_lat;
            sumLat += to_lat;
            summed += 2;
        }
        writer.print(getLeafletHeader(sumLat / summed, sumLon / summed));
        //TODO - is there any nicer way to find max and min?
        Integer max = Integer.MIN_VALUE;
        Integer min = Integer.MAX_VALUE;
        for(Edge e: ways){
            if(e.rateWay() != null){
                max = Math.max(max, e.rateWay());
                min = Math.min(min, e.rateWay());
            }
        }
        for(Edge e: ways){
            writer.println(getLeafletPolylineStringFromEdge(e, max, min));
        }
        writer.print(getLeafletFooter());
        writer.close();
    }

    private String getLeafletPolylineStringFromEdge(Edge e, Integer max, Integer min) {
        Double from_lat = nodes.get(e.from).location.latitude;
        Double from_lon = nodes.get(e.from).location.longitude;
        Double to_lat = nodes.get(e.to).location.latitude;
        Double to_lon = nodes.get(e.to).location.longitude;
        Integer rescaled = 100;
        if(!Objects.equals(max, min)){
            rescaled = (100 * ((e.rateWay()-min)/(max-min)));
        }
        return "L.polyline([["+from_lat+","+from_lon+"], ["+to_lat+","+to_lon+"]], {color: \""+getColor(rescaled)+"\", opacity: 1}).addTo(map); //"+e.from + " to " + e.to;
    }

    public static String getColor(int percent) {
        //from http://stackoverflow.com/questions/340209/generate-colors-between-red-and-green-for-a-power-meter
        float hue = (float) (percent/100.0*0.4); // 0.4 = Green, see chart in page linked above
        float saturation = 0.9f;
        float brightness = 0.9f;

        int intColor = Color.HSBtoRGB(hue, saturation, brightness);
        return String.format("#%06X", (0xFFFFFF & intColor)); //Color.getHSBColor((float)H, (float)S, (float)B);
    }

    private String getLeafletHeader(Double latitudeCenter, Double longitudeCenter) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<title>Leaflet Quick Start Guide Example</title>\n" +
                "\t<meta charset=\"utf-8\" />\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "\t<link rel=\"stylesheet\" href=\"http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css\" />\n" +
                "    <style>\n" +
                "        body {\n" +
                "            padding: 0;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "        html, body, #map {\n" +
                "            height: 100%;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<div id=\"map\"></div>\n" +
                "\n" +
                "\t<script src=\"http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js\"></script>\n" +
                "\t<script>\n" +
                "\t\tvar map = L.map('map').setView(["+latitudeCenter+", "+longitudeCenter+"], 15);\n" +
                "\t\tmapLink = '<a href=\"http://openstreetmap.org\">OpenStreetMap</a>';\n"+
                getOSMDefaultStyleTiles();
    }
    private String getOSMDefaultStyleTiles() {
        return  "\t\tL.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "\t\t\tmaxZoom: 18,\n" +
                "\t\t\tattribution: '&copy; ' + mapLink + ' Contributors'\n" +
                "\t\t}).addTo(map);\n";
    }

    private String getBLackAndWhiteMapTiles(){
        return  "L.tileLayer('http://openmapsurfer.uni-hd.de/tiles/roadsg/x={x}&y={y}&z={z}', {\n" +
                "\t\t\tminZoom: 0,\n" +
                "\t\t\tmaxZoom: 19,\n" +
                "\t\t\tattribution: 'Imagery from <a href=\"http://giscience.uni-hd.de/\">GIScience Research Group @ University of Heidelberg</a> &mdash; Map data &copy; <a href=\"http://www.openstreetmap.org/copyright\">OpenStreetMap</a>'\n" +
                "\t\t}).addTo(map);\n";
    }

    private String getLeafletFooter() {
        return "\t</script>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public void merge(Graph added){
        nodes.putAll(added.nodes);
        ways.addAll(added.ways);
    }
}
