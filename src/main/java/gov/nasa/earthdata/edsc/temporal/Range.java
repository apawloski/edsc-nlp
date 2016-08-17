package gov.nasa.earthdata.edsc.temporal;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author bsun
 */
public class Range {

    private String begin;
    private String end;
    private String duration;
    private int periodicity;

    public Range(String begin, String end, String duration) {
        this.begin = begin;
        this.end = end;
        this.duration = duration;
        this.periodicity = Integer.MIN_VALUE;
    }

    public Range(String begin, String end, String duration, int periodicity) {
        this.begin = begin;
        this.end = end;
        this.duration = duration;
        this.periodicity = periodicity;
    }

    public Range() {
        Calendar c = Calendar.getInstance();
        c.clear();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        c.set(Calendar.YEAR, c.getActualMinimum(Calendar.YEAR));
        c.set(Calendar.MONTH, c.getActualMinimum(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        this.begin = formatter.format(c.getTime());
        c.set(Calendar.YEAR, c.getActualMaximum(Calendar.YEAR));
        c.set(Calendar.MONTH, c.getActualMaximum(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        this.end = formatter.format(c.getTime());
        this.duration = "";
        this.periodicity = Integer.MIN_VALUE;
    }

    /**
     * @return the begin
     */
    public String getBegin() {
        return begin;
    }

    /**
     * @param begin the begin to set
     */
    public void setBegin(String begin) {
        this.begin = begin;
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
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String toString() {
        return "(" + this.begin + "," + this.end + "," + this.duration + "," + this.periodicity + ")";
    }

    /**
     * @return the periodicity
     */
    public int getPeriodicity() {
        return periodicity;
    }

    /**
     * @param periodicity the periodicity to set
     */
    public void setPeriodicity(int periodicity) {
        this.periodicity = periodicity;
    }
}
