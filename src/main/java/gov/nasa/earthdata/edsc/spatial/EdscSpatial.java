/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nasa.earthdata.edsc.spatial;

/**
 *
 * @author bsun
 */
public class EdscSpatial {
    private String textAfterExtraction;
    private String geonames;
    private EdscBoundingBox bbox;
    private String query;

    /**
     * @return the geonames
     */
    public String getGeonames() {
        return geonames;
    }

    /**
     * @param geonames the geonames to set
     */
    public void setGeonames(String geonames) {
        this.geonames = geonames;
    }

    /**
     * @return the bbox
     */
    public EdscBoundingBox getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     */
    public void setBbox(EdscBoundingBox bbox) {
        this.bbox = bbox;
    }

    /**
     * @return the textAfterExtraction
     */
    public String getTextAfterExtraction() {
        return textAfterExtraction;
    }

    /**
     * @param textAfterExtraction the textAfterExtraction to set
     */
    public void setTextAfterExtraction(String textAfterExtraction) {
        this.textAfterExtraction = textAfterExtraction;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }
}
