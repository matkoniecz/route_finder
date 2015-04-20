package graph;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Graph {
    HashMap<Long, Vertex> nodes;
    HashMap<Long, Edge> ways;
    public Graph(String pathToOSMXmlFile){
        nodes = new HashMap<>();
        ways = new HashMap<>();
        Boolean nodes_processed = false;
        Long previous_node = null;
        Long id = null;
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
                            System.out.println(type);
                            Double lat = new Double(startElement.getAttributeByName(new QName("lat")).getValue());
                            Double lon = new Double(startElement.getAttributeByName(new QName("lon")).getValue());
                            id = new Long(startElement.getAttributeByName(new QName("id")).getValue());
                            Vertex node = new Vertex(lat, lon);
                            nodes.put(id, node);
                            break;
                        }
                        case "way":
                            System.out.println(type);
                            id = new Long(startElement.getAttributeByName(new QName("id")).getValue());
                            previous_node = null;
                            nodes_processed = true;
                            break;
                        case "nd": {
                            System.out.println(type);
                            Long node_id = new Long(startElement.getAttributeByName(new QName("ref")).getValue());
                            System.out.println(previous_node);
                            System.out.println(node_id);
                            if (previous_node != null) {
                                ways.put(fake_way_id++, new Edge(previous_node, node_id));
                                ways.put(fake_way_id++, new Edge(node_id, previous_node));
                            }
                            previous_node = node_id;
                            break;
                        }
                        case "tag":
                            //TODO add only ways with highway type
                            break;
                    }
                    xmlEvent = xmlEventReader.nextEvent();
                }
                if(xmlEvent.isEndElement()){
                    //end element is reached
                }
            }

        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void generateLeafletHtmlView(String filename) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.print(getLeafletHeader());
        for(Edge e: ways.values()){
            try{
                System.out.println(e.from);
                System.out.println(e.to);
                Double from_lat = nodes.get(e.from).lat;
                Double from_lon = nodes.get(e.from).lon;
                Double to_lat = nodes.get(e.to).lat;
                Double to_lon = nodes.get(e.to).lon;
                String polyline = "L.polyline([["+from_lat+","+from_lon+"], ["+to_lat+","+to_lon+"]]).addTo(map); //"+e.from + " to " + e.to;
                System.out.println(polyline);
                writer.println(polyline);
            } catch(NullPointerException exception){
                //exception.printStackTrace();
                System.err.println(e.from + " to " + e.to);
                //TODO handle this properly (expected for ways going outside downloaded area)
            }
        }
        writer.print(getLeafletFooter());
        writer.close();
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
                "\t\tmapLink = '<a href=\"http://openstreetmap.org\">OpenStreetMap</a>';\n" +
                "\t\tL.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "\t\t\tmaxZoom: 18,\n" +
                "\t\t\tattribution: '&copy; ' + mapLink + ' Contributors',\n" +
                "\t\t}).addTo(map);\n";
    }

    private String getLeafletFooter() {
        return "\t</script>\n" +
                "</body>\n" +
                "</html>\n";
    }
}
