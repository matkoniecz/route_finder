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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Graph {
    HashMap<Long, Vertex> nodes;
    HashMap<Long, Edge> ways;
    public Graph(String pathToOSMXmlFile){
        nodes = new HashMap<>();
        ways = new HashMap<>();
        HashMap<Long, Edge> potential_ways = new HashMap<>();
        HashMap<String, String> tags = new HashMap<>();
        Long previous_node = null;
        Long id;
        Long fake_way_id = Long.parseLong("0");
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
                            if(potential_ways.size()>0){
                                //System.out.println(tags);
                                if(tags.containsKey("highway")){
                                    if(roads.contains(tags.get("highway"))){
                                        ways.putAll(potential_ways);
                                    }
                                }
                            }
                            potential_ways = new HashMap<>();
                            tags = new HashMap<>();

                            //System.out.println(type);
                            id = new Long(startElement.getAttributeByName(new QName("id")).getValue());
                            previous_node = null;
                            break;
                        case "nd": {
                            //System.out.println(type);
                            Long node_id = new Long(startElement.getAttributeByName(new QName("ref")).getValue());
                            //System.out.println(previous_node);
                            //System.out.println(node_id);
                            if (previous_node != null) {
                                potential_ways.put(fake_way_id++, new Edge(previous_node, node_id, tags, nodes));
                                potential_ways.put(fake_way_id++, new Edge(node_id, previous_node, tags, nodes));
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

    public void generateLeafletHtmlView(String filename) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.print(getLeafletHeader());
        //TODO - is there any nicer way to find max and min?
        Integer max = -1;
        Integer min = 1000000000;
        for(Edge e: ways.values()){
            if(e.rateWay() != null){
                max = Math.max(max, e.rateWay());
                min = Math.min(min, e.rateWay());
            }
        }
        for(Edge e: ways.values()){
            try{
                //System.out.println(e.from);
                //System.out.println(e.to);
                Double from_lat = nodes.get(e.from).location.latitude;
                Double from_lon = nodes.get(e.from).location.longitude;
                Double to_lat = nodes.get(e.to).location.latitude;
                Double to_lon = nodes.get(e.to).location.longitude;
                Integer rescaled = (100 * ((e.rateWay()-min)/(max-min)));
                String polyline = "L.polyline([["+from_lat+","+from_lon+"], ["+to_lat+","+to_lon+"]], {color: \""+getColor(rescaled)+"\"}).addTo(map); //"+e.from + " to " + e.to;
                //System.out.println(polyline);
                writer.println(polyline);
            } catch(NullPointerException exception){
                //exception.printStackTrace();
                //System.err.println(e.from + " to " + e.to);
                //TODO handle this properly (expected for ways going outside downloaded area)
            }
        }
        writer.print(getLeafletFooter());
        writer.close();
    }

    public static String getColor(int percent) {
        //from http://stackoverflow.com/questions/340209/generate-colors-between-red-and-green-for-a-power-meter
        System.out.println(percent/100.0);
        double H = percent/100.0*0.4; // Hue (note 0.4 = Green, see huge chart below)
        double S = 0.9; // Saturation
        double B = 0.9; // Brightness

        int intColor = Color.HSBtoRGB((float) H, (float) S, (float) B);
        String hexColor = String.format("#%06X", (0xFFFFFF & intColor));
        //System.out.println(hexColor);
        return hexColor; //Color.getHSBColor((float)H, (float)S, (float)B);
    }

    private String getLeafletHeader() {
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
                "\t\tvar map = L.map('map').setView([50, 20], 18);\n" +
                "\t\tmapLink = '<a href=\"http://openstreetmap.org\">OpenStreetMap</a>';\n"+
                getOSMDefaultStyleTiles();
    }
    private String getOSMDefaultStyleTiles() {
        return  "\t\tL.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "\t\t\tmaxZoom: 18,\n" +
                "\t\t\tattribution: '&copy; ' + mapLink + ' Contributors',\n" +
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
}
