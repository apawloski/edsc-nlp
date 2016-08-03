package gov.nasa.earthdata.edsc.spatial;

/**
 *
 * @author bsun
 */
public class EdscBoundingBox {
    private EdscPoint swPoint;
    private EdscPoint nePoint;
    
    public EdscBoundingBox() {
        
    }
    
    public EdscBoundingBox(EdscPoint sw, EdscPoint ne) {
        this.nePoint = ne;
        this.swPoint = sw;
    }

    /**
     * @return the swPoint
     */
    public EdscPoint getSwPoint() {
        return swPoint;
    }

    /**
     * @param swPoint the swPoint to set
     */
    public void setSwPoint(EdscPoint swPoint) {
        this.swPoint = swPoint;
    }

    /**
     * @return the nePoint
     */
    public EdscPoint getNePoint() {
        return nePoint;
    }

    /**
     * @param nePoint the nePoint to set
     */
    public void setNePoint(EdscPoint nePoint) {
        this.nePoint = nePoint;
    }
}
