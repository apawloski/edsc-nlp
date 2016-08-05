package gov.nasa.earthdata.edsc.temporal;

/**
 *
 * @author bsun
 */
public class EdscTemporal {
    private String textAfterExtraction;
    private String temporal;
    private String timex;
    private String query;

    /**
     * @return the temporal
     */
    public String getTemporal() {
        return temporal;
    }

    /**
     * @param temporal the temporal to set
     */
    public void setTemporal(String temporal) {
        this.temporal = temporal;
    }

    /**
     * @return the timex
     */
    public String getTimex() {
        return timex;
    }

    /**
     * @param timex the timex to set
     */
    public void setTimex(String timex) {
        this.timex = timex;
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
}
