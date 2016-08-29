package gov.nasa.earthdata.edsc.nlp.utils;

import gov.nasa.earthdata.edsc.nlp.spatial.EdscSpatial;
import gov.nasa.earthdata.edsc.nlp.temporal.EdscTemporal;

public class EdscResponse {

    private EdscSpatial edscSpatial;
    private EdscTemporal edscTemporal;
    private String keyword;

    /**
     * @return the edscSpatial
     */
    public EdscSpatial getEdscSpatial() {
        return edscSpatial;
    }

    /**
     * @param edscSpatial the edscSpatial to set
     */
    public void setEdscSpatial(EdscSpatial edscSpatial) {
        this.edscSpatial = edscSpatial;
    }

    /**
     * @return the edscTemporal
     */
    public EdscTemporal getEdscTemporal() {
        return edscTemporal;
    }

    /**
     * @param edscTemporal the edscTemporal to set
     */
    public void setEdscTemporal(EdscTemporal edscTemporal) {
        this.edscTemporal = edscTemporal;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
