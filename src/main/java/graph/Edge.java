package graph;

public class Edge {
    Long from;
    Long to;
    Integer attractiveness;
    Integer length;

    public Edge(Long from, Long to) {
        this.from = from;
        this.to = to;
        attractiveness = 1; //TODO - compute
        length = 1; //TODO - compute
    }
}
