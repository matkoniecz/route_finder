import org.junit.Test;
import osm_processing.DistanceCalculator;
import static org.junit.Assert.*;

public class DistanceCalculatorTest {
    @Test
    public void testGetDistanceBetweenCoordinates() throws Exception {
        double almostZeroInKilometers = 1.0/1000; //1 m error is perfectly acceptable

        String distanceToItself = "point has 0 distance to itself";
        for(int i = 0; i<1000; i++){
            double lat = Math.random() * 180 - 90;
            double lon = Math.random() * 180 - 90;
            assertEquals(distanceToItself, 0, DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(lat, lon, lat, lon), almostZeroInKilometers);
        }

        double distance;
        //expected values were acquired from http://www.movable-type.co.uk/scripts/latlong.html
        distance = DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(50.03, 5.42, 58.38, 3.04);
        assertEquals("distance check", 941.1, distance, 0.05);
        distance = DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(0, 0, 90, 0);
        assertEquals("distance check", 10010, distance, 5);
        distance = DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(0, 0, 0, 180);
        assertEquals("distance check", 20020, distance, 5);
        distance = DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(-50.03, 5.42, 58.38, 3.04);
        assertEquals("distance check", 12060, distance, 5);
        distance = DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(50.0764, 19.9658, 19.9658, 3);
        assertEquals("distance check", 3668, distance, 0.5);
        distance = DistanceCalculator.getDistanceBetweenCoordinatesInKilometers(50.0764, 19.9658, 51.0764, 20.9658);
        assertEquals("distance check", 131.7, distance, 0.05);
    }
}
