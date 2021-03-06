package gov.nasa.earthdata.edsc.nlp.spatial;

public class Point {
    private double latitude;
    private double longitude;

    public Point() {    
    }
    
    public Point(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }
    
    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
