package gov.nasa.earthdata.edsc.nlp.spatial;

public class BoundingBox {

    private Point swPoint;
    private Point nePoint;

    public BoundingBox() {

    }

    public BoundingBox(Point sw, Point ne) {
        this.nePoint = ne;
        this.swPoint = sw;
    }

    /**
     * @return the swPoint
     */
    public Point getSwPoint() {
        return swPoint;
    }

    /**
     * @param swPoint the swPoint to set
     */
    public void setSwPoint(Point swPoint) {
        this.swPoint = swPoint;
    }

    /**
     * @return the nePoint
     */
    public Point getNePoint() {
        return nePoint;
    }

    /**
     * @param nePoint the nePoint to set
     */
    public void setNePoint(Point nePoint) {
        this.nePoint = nePoint;
    }
}
