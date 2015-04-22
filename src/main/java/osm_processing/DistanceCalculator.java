package osm_processing;

public final class DistanceCalculator {
    private static final double EARTH_RADIUS = 6371;

    private DistanceCalculator() {
    }

    public static double getDistanceBetweenCoordinates(Coordinate loc1, Coordinate loc2) {
        return getDistanceBetweenCoordinates(loc1.latitude, loc1.longitude, loc2.latitude, loc2.longitude);
    }

    /**
     * Computes distance between two points on Earth using Haversine formula
     * <p/>
     * Earth is assumed to be sphere, errors from assuming spherical geometry might be up to 0.55% crossing the equator
     * source: https://en.wikipedia.org/wiki/Haversine_formula and http://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param lat1 latitude of the first point
     * @param lon1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lon2 longitude of the second point
     * @return Distance between points, in kilometers
     */
    public static double getDistanceBetweenCoordinates(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a; //square of half the chord length between the points
        a = Math.sin(dLat / 2) * Math.sin(dLat / 2);
        a += Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat2) * Math.cos(lat1);
        double angularDistanceInRadians = Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 2 * EARTH_RADIUS * angularDistanceInRadians;
    }
}
