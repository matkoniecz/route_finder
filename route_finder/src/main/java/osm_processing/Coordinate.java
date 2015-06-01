package osm_processing;

import java.io.Serializable;

public class Coordinate implements Serializable {
    public double latitude;
    public double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "latitude=" + this.latitude +
                ", longitude=" + this.longitude +
                '}';
    }


}
