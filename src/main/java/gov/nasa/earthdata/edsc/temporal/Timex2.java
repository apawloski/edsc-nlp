package gov.nasa.earthdata.edsc.temporal;

import java.util.Calendar;
import java.util.regex.Pattern;
import org.w3c.dom.Element;

/**
 *
 * @author bsun
 */
public class Timex2 {

    private final String val;
    private final String type;
    private final int beginPoint;
    private final int endPoint;
    private final String xml;

    public Timex2(String val) {
        this(null, val);
    }

    public Timex2(String type, String val) {
        this.val = val;
        this.type = type;
        this.beginPoint = -1;
        this.endPoint = -1;
        this.xml = (val == null ? "<TIMEX3/>" : String.format("<TIMEX3 VAL=\"%s\" TYPE=\"%s\"/>", this.val, this.type));
    }
    
    public Pair<Calendar, Calendar> getRange() {
        return getRange(Calendar.getInstance());
    }

    public Pair<Calendar, Calendar> getRange(Calendar documentTime) {
        
        if (this.type != null && this.type == "SET") {
            
        }
        
        /*
        *
        !!! ORDER MATTERS !!!
        *
        */
        
        if (this.val == null) {
            throw new UnsupportedOperationException("no value specified for " + this);
        } // YYYYMMDD or YYYYMMDDT... where the time is concatenated directly with the
        // date
        else if (val.length() >= 8 && Pattern.matches("\\d\\d\\d\\d\\d\\d\\d\\d", this.val.substring(0, 8))) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            int month = Integer.parseInt(this.val.substring(4, 6));
            int day = Integer.parseInt(this.val.substring(6, 8));
            return new Pair<>(makeCalendar(year, month, day), makeCalendar(year, month, day));
        } // YYYY-MM-DD or YYYY-MM-DDT...
        else if (val.length() >= 10 && Pattern.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d", this.val.substring(0, 10))) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            int month = Integer.parseInt(this.val.substring(5, 7));
            int day = Integer.parseInt(this.val.substring(8, 10));
            return new Pair<>(makeCalendar(year, month, day), makeCalendar(year, month, day));
        } // YYYYMMDDL+
        else if (Pattern.matches("\\d\\d\\d\\d\\d\\d\\d\\d[A-Z]+", this.val)) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            int month = Integer.parseInt(this.val.substring(4, 6));
            int day = Integer.parseInt(this.val.substring(6, 8));
            return new Pair<>(makeCalendar(year, month, day), makeCalendar(year, month, day));
        } // YYYYMM or YYYYMMT...
        else if (val.length() >= 6 && Pattern.matches("\\d\\d\\d\\d\\d\\d", this.val.substring(0, 6))) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            int month = Integer.parseInt(this.val.substring(4, 6));
            Calendar begin = makeCalendar(year, month, 1);
            int lastDay = begin.getActualMaximum(Calendar.DATE);
            Calendar end = makeCalendar(year, month, lastDay);
            return new Pair<>(begin, end);
        } // YYYY-MM or YYYY-MMT...
        else if (val.length() >= 7 && Pattern.matches("\\d\\d\\d\\d-\\d\\d", this.val.substring(0, 7))) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            int month = Integer.parseInt(this.val.substring(5, 7));
            Calendar begin = makeCalendar(year, month, 1);
            int lastDay = begin.getActualMaximum(Calendar.DATE);
            Calendar end = makeCalendar(year, month, lastDay);
            return new Pair<>(begin, end);
        } 

        // PDDY
        if (Pattern.matches("P\\d+Y", this.val) && documentTime != null) {

            int yearRange = Integer.parseInt(this.val.substring(1, this.val.length() - 1));

            // in the future
            if (this.beginPoint < this.endPoint) {
                Calendar start = copyCalendar(documentTime);
                Calendar end = copyCalendar(documentTime);
                end.add(Calendar.YEAR, yearRange);
                return new Pair<>(start, end);
            } // in the past
            else if (this.beginPoint > this.endPoint) {
                Calendar start = copyCalendar(documentTime);
                Calendar end = copyCalendar(documentTime);
                start.add(Calendar.YEAR, 0 - yearRange);
                return new Pair<>(start, end);
            }

            throw new RuntimeException("begin and end are equal " + this);
        }
        // PDDM
        if (Pattern.matches("P\\d+M", this.val) && documentTime != null) {
            int monthRange = Integer.parseInt(this.val.substring(1, this.val.length() - 1));

            // in the future
            if (this.beginPoint < this.endPoint) {
                Calendar start = copyCalendar(documentTime);
                Calendar end = copyCalendar(documentTime);
                end.add(Calendar.MONTH, monthRange);
                return new Pair<>(start, end);
            }

            // in the past
            if (this.beginPoint > this.endPoint) {
                Calendar start = copyCalendar(documentTime);
                Calendar end = copyCalendar(documentTime);
                start.add(Calendar.MONTH, 0 - monthRange);
                return new Pair<>(start, end);
            }

            throw new RuntimeException("begin and end are equal " + this);
        }
        // PDDD
        if (Pattern.matches("P\\d+D", this.val) && documentTime != null) {
            int dayRange = Integer.parseInt(this.val.substring(1, this.val.length() - 1));

            // in the future
            if (this.beginPoint < this.endPoint) {
                Calendar start = copyCalendar(documentTime);
                Calendar end = copyCalendar(documentTime);
                end.add(Calendar.DAY_OF_MONTH, dayRange);
                return new Pair<>(start, end);
            }

            // in the past
            if (this.beginPoint > this.endPoint) {
                Calendar start = copyCalendar(documentTime);
                Calendar end = copyCalendar(documentTime);
                start.add(Calendar.DAY_OF_MONTH, 0 - dayRange);
                return new Pair<>(start, end);
            }

            throw new RuntimeException("begin and end are equal " + this);
        }

        // YYYYSP
        if (Pattern.matches("\\d+-SP", this.val)) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            Calendar start = makeCalendar(year, 2, 1);
            Calendar end = makeCalendar(year, 4, 31);
            return new Pair<>(start, end);
        }
        // YYYYSU
        if (Pattern.matches("\\d+-SU", this.val)) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            Calendar start = makeCalendar(year, 5, 1);
            Calendar end = makeCalendar(year, 7, 31);
            return new Pair<>(start, end);
        }
        // YYYYFA
        if (Pattern.matches("\\d+-FA", this.val)) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            Calendar start = makeCalendar(year, 8, 1);
            Calendar end = makeCalendar(year, 10, 31);
            return new Pair<>(start, end);
        }
        // YYYYWI
        if (Pattern.matches("\\d+-WI", this.val)) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            Calendar start = makeCalendar(year, 11, 1);
            Calendar end = makeCalendar(year + 1, 1, 29);
            return new Pair<>(start, end);
        }

        // YYYYWDD
        if (Pattern.matches("\\d\\d\\d\\dW\\d+", this.val)) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            int week = Integer.parseInt(this.val.substring(5));
            int startDay = (week - 1) * 7;
            int endDay = startDay + 6;
            Calendar start = makeCalendar(year, startDay);
            Calendar end = makeCalendar(year, endDay);
            return new Pair<>(start, end);
        }
        
        // YYYY or YYYYT...
        if (val.length() >= 4 && Pattern.matches("\\d\\d\\d\\d", this.val.substring(0, 4))) {
            int year = Integer.parseInt(this.val.substring(0, 4));
            return new Pair<>(makeCalendar(year, 1, 1), makeCalendar(year, 12, 31));
        }

        // PRESENT_REF
        if (this.val.equals("PRESENT_REF")) {
            Calendar start = copyCalendar(documentTime);
            Calendar end = copyCalendar(documentTime);
            return new Pair<>(start, end);
        }

        throw new RuntimeException(String.format("unknown value \"%s\" in %s", this.val, this));
    }

    private static Calendar makeCalendar(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(year, month - 1, day, 0, 0, 0);
        return date;
    }

    private static Calendar makeCalendar(int year, int dayOfYear) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.DAY_OF_YEAR, dayOfYear);
        return date;
    }

    private static Calendar copyCalendar(Calendar c) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c
                .get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return date;
    }
}
