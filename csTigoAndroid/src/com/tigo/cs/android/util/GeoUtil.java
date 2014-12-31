package com.tigo.cs.android.util;

public class GeoUtil {
    public static final double EARTH_RADIUS = 6371000; // meters

    /**
     * 
     * @param geoPoint
     *            GeoPoint to move in degrees.
     * @param azimuth
     *            Azimuth in degrees
     * @param distance
     *            Distance in meters
     * @return Moved GeoPoint in degrees
     */
    public static GeoPoint moveGeoPoint(GeoPoint geoPoint, double azimuth, double distance) {
        return moveGeoPoint(geoPoint.getLatitude(), geoPoint.getLongitude(),
                azimuth, distance);
    }

    /**
     * 
     * @param latitude
     *            Latitude in degrees
     * @param longitude
     *            Longitude in degrees
     * @param azimuth
     *            Azimuth in degrees
     * @param distance
     *            Distance in meters
     * @return Moved GeoPoint in degrees
     */
    public static GeoPoint moveGeoPoint(double latitude, double longitude, double azimuth, double distance) {
        // Convert data from degrees to radians
        latitude = Math.toRadians(latitude); // Convert to radians
        longitude = Math.toRadians(longitude); // Convert to radians
        distance = distance / EARTH_RADIUS; // Convert to angular distance in
                                            // radians
        azimuth = Math.toRadians(azimuth); // Convert to radians
        double newLatitude = latitude; // In radians
        double newLongitude = longitude; // In radians

        // Calculate new geo point
        newLatitude = Math.asin(Math.sin(latitude) * Math.cos(distance)
            + Math.cos(latitude) * Math.sin(distance) * Math.cos(azimuth));
        newLongitude = longitude
            + Math.atan2(
                    Math.sin(azimuth) * Math.sin(distance) * Math.cos(latitude),
                    Math.cos(distance) - Math.sin(latitude)
                        * Math.sin(newLatitude));
        newLongitude = (newLongitude + 3 * Math.PI) % (2 * Math.PI) - Math.PI; // Normalise
                                                                               // to
                                                                               // -180
                                                                               // ...
                                                                               // +180

        // Conver new data from radians to degrees
        newLatitude = Math.toDegrees(newLatitude); // Convert to degrees
        newLongitude = Math.toDegrees(newLongitude); // Convert to degrees

        return new GeoPoint(newLatitude, newLongitude);
    }

    // Class for manage geo points
    public static class GeoPoint {

        private double latitude = 0;
        private double longitude = 0;

        public GeoPoint() {
        }

        public GeoPoint(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
