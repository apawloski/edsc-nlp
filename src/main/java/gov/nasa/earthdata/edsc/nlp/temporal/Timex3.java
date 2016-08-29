package gov.nasa.earthdata.edsc.nlp.temporal;

import gov.nasa.earthdata.edsc.nlp.utils.XmlUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;
import org.w3c.dom.Element;

public class Timex3 {

    private String val;
    private String type;
    private int beginPoint;
    private int endPoint;
    private String xml;
    private Range range;
    private String tid;

    public Timex3(String xml) {
        this.val = null;
        this.type = null;
        this.beginPoint = -1;
        this.endPoint = -1;
        this.xml = null;
    }

    public Timex3(String type, String val) {
        this.val = val;
        this.type = type;
        this.beginPoint = -1;
        this.endPoint = -1;
        this.xml = (val == null ? "<TIMEX3/>" : String.format("<TIMEX3 VAL=\"%s\" TYPE=\"%s\"/>", this.val, this.type));
    }

    public Timex3() {
        this.val = null;
        this.type = null;
        this.beginPoint = -1;
        this.endPoint = -1;
        this.xml = null;
    }

    public static Timex3 fromXml(String xml) {
        Element element = XmlUtils.parseElement(xml);
        if ("TIMEX3".equals(element.getNodeName())) {
            Timex3 t = new Timex3();
            t.init(element);
            return t;
        } else {
            throw new IllegalArgumentException("Invalid timex xml: " + xml);
        }
    }

    /**
     * @return the val
     */
    public String getVal() {
        return val;
    }

    /**
     * @param val the val to set
     */
    public void setVal(String val) {
        this.val = val;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the beginPoint
     */
    public int getBeginPoint() {
        return beginPoint;
    }

    /**
     * @param beginPoint the beginPoint to set
     */
    public void setBeginPoint(int beginPoint) {
        this.beginPoint = beginPoint;
    }

    /**
     * @return the endPoint
     */
    public int getEndPoint() {
        return endPoint;
    }

    /**
     * @param endPoint the endPoint to set
     */
    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * @return the xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml the xml to set
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * @param range the range to set
     */
    public void setRange(Range range) {
        this.range = range;
    }

    /**
     * @return the range
     */
    public Range getRange() {
        return range;
    }

    /**
     * @return the tid
     */
    public String getTid() {
        return tid;
    }

    /**
     * @param tid the tid to set
     */
    public void setTid(String tid) {
        this.tid = tid;
    }

    private void init(Element element) {
        init(XmlUtils.nodeToString(element, false), element);
    }

    private void init(String xml, Element element) {
        this.xml = xml;

        // Mandatory attributes
        this.tid = XmlUtils.getAttribute(element, "tid");
        this.val = XmlUtils.getAttribute(element, "VAL");
        if (this.val == null) {
            this.val = XmlUtils.getAttribute(element, "value");
        }
        this.type = XmlUtils.getAttribute(element, "type");
        if (type == null) {
            this.type = XmlUtils.getAttribute(element, "TYPE");
        }

        // Optional attributes
        String begin = XmlUtils.getAttribute(element, "beginPoint");
        this.beginPoint = (begin == null || begin.length() == 0) ? -1 : Integer.parseInt(begin.substring(1));
        String end = XmlUtils.getAttribute(element, "endPoint");
        this.endPoint = (end == null || end.length() == 0) ? -1 : Integer.parseInt(end.substring(1));

        // Optional range
        String rangeStr = XmlUtils.getAttribute(element, "range");
        if (rangeStr != null) {
            if (rangeStr.startsWith("(") && rangeStr.endsWith(")")) {
                rangeStr = rangeStr.substring(1, rangeStr.length() - 1);
            }
            String[] parts = rangeStr.split(",");
            /*
            * Need to work around several bugs here...
            *  - issue #224 (https://github.com/stanfordnlp/CoreNLP/issues/244)
            *  - issue #235 (https://github.com/stanfordnlp/CoreNLP/issues/235)
             */
            // #235 incorrect for current and future years
            if (Pattern.matches(".*>last week</timex3>", xml.toLowerCase())) {
                int year = Integer.parseInt(this.val.substring(0, 4));
                Calendar c = Calendar.getInstance();
                if (year >= c.get(Calendar.YEAR)) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    int weekOfYear = Integer.parseInt(this.val.substring(6));
                    c.clear();
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.WEEK_OF_YEAR, weekOfYear + 1);
                    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    parts[0] = formatter.format(c.getTime());
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.WEEK_OF_YEAR, weekOfYear + 2);
                    c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    parts[1] = formatter.format(c.getTime());
                }
            }
            // #244
            if (Pattern.matches("\\d{4,}-WI", this.val)) {
                int year = Integer.parseInt(this.val.substring(0, 4)) + 1;
                parts[1] = year + parts[1].substring(4);
            }
            this.range = new Range(parts.length > 0 ? parts[0] : "", parts.length > 1 ? parts[1] : "", parts.length > 2 ? parts[2] : "");
        }
    }
}
