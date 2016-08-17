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
    private String start;
    private String end;
    private boolean recurring;

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

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * @return the recurring
     */
    public boolean isRecurring() {
        return recurring;
    }

    /**
     * @param recurring the recurring to set
     */
    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }
}
