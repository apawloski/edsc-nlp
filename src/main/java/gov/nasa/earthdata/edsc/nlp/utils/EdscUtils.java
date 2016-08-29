package gov.nasa.earthdata.edsc.nlp.utils;

import com.bericotech.clavin.resolver.ResolvedLocation;
import gov.nasa.earthdata.edsc.nlp.rest.core.ResolvedLocations;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;
import gov.nasa.earthdata.edsc.nlp.utils.EdscPrepositions.Prepositions;
import gov.nasa.earthdata.edsc.nlp.spatial.BoundingBox;
import gov.nasa.earthdata.edsc.nlp.spatial.Point;
import gov.nasa.earthdata.edsc.nlp.spatial.EdscSpatial;
import gov.nasa.earthdata.edsc.nlp.temporal.EdscTemporal;
import gov.nasa.earthdata.edsc.nlp.temporal.Timex3;
import gov.nasa.earthdata.edsc.nlp.temporal.Range;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bsun
 */
public class EdscUtils {

    private static final Logger logger = LoggerFactory.getLogger(EdscUtils.class);

    private static String removeTrailingPreposition(String text) {
        for (Prepositions prep : Prepositions.values()) {
            if (text.toLowerCase().endsWith(" " + prep.value())) {
                return removeTrailingPreposition(text.substring(0, text.length() - (" " + prep.value()).length()));
            }
        }
        return text;
    }

    public static EdscSpatial spatialParsing(String text, List<ResolvedLocation> resolvedLocations) throws UnsupportedEncodingException, JSONException {
        /*
         * Spatial Extraction
         */
        ResolvedLocations result = new ResolvedLocations(resolvedLocations);
        String geoName = "";
        String delim = "";
        String country = "";
        boolean isCountry = false;
        for (ResolvedLocation rLoc : resolvedLocations) {
            geoName += delim + rLoc.getMatchedName();
            delim = ",";

            // Remove matched spatial (and prepositions) from text
            if (text.toLowerCase().contains(rLoc.getMatchedName().toLowerCase())) {
                String[] splits = text.toLowerCase().split("[^A-Za-z0-9]*" + rLoc.getMatchedName().toLowerCase() + "[^A-Za-z0-9]*", 2);
                text = removeTrailingPreposition(splits[0]) + " " + splits[1];
                text = text.trim();
            }

            // Add country to the extracted geoname string
            if (rLoc.getGeoname().getPrimaryCountryName().equals(rLoc.getMatchedName())) {
                isCountry = true;
            }
            if (!isCountry && country.isEmpty() && !rLoc.getGeoname().getPrimaryCountryName().isEmpty()) {
                country = rLoc.getGeoname().getPrimaryCountryName();
            }
        }
        if (!country.isEmpty()) {
            geoName += "," + country;
        }
        logger.info("---- geoName: " + geoName);

        if (resolvedLocations.size() > 0) {
            String uri = "http://api.geonames.org/search?username=edsc&type=json&maxRows=1&isNameRequired=true&style=full&q=" + URLEncoder.encode(geoName, "UTF-8");
            logger.info("Seding request to geonames.org: " + uri);
            ClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
            Client client = Client.create(clientConfig);
            WebResource resource = client.resource(uri);
            ClientResponse response = resource.accept("application/json").get(ClientResponse.class);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                JSONObject json = new JSONObject(response.getEntity(String.class));
                JSONArray geonamesResponse = json.getJSONArray("geonames");
                for (int i = 0; i < geonamesResponse.length(); i++) {
                    Iterator<String> keys = geonamesResponse.getJSONObject(i).keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (key.equals("bbox")) {
                            JSONObject bbox = geonamesResponse.getJSONObject(i).optJSONObject(key);
                            Point nePoint = new Point(bbox.getDouble("north"), bbox.getDouble("east"));
                            Point swPoint = new Point(bbox.getDouble("south"), bbox.getDouble("west"));
                            EdscSpatial spatial = new EdscSpatial();
                            spatial.setBbox(new BoundingBox(swPoint, nePoint));
                            spatial.setGeonames(geoName);
                            spatial.setTextAfterExtraction(text);
                            spatial.setQuery("bounding_box:" + swPoint.getLongitude() + "," + swPoint.getLatitude() + ":" + nePoint.getLongitude() + "," + nePoint.getLatitude());
                            return spatial;
                        }
                    }
                }
            } else {
                logger.error("Request '" + uri + "' returned status code: " + response.getStatus());
                return null;
            }
        }

        logger.info("No resolved locations found from text: " + text);
        return null;
    }

    public static EdscTemporal temporalParsing(String text, AnnotationPipeline pipeline) {
        Annotation annotation = new Annotation(text);
        annotation.set(CoreAnnotations.DocDateAnnotation.class,
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        pipeline.annotate(annotation);
        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);

        String out = "";
        EdscTemporal edscTemporal = new EdscTemporal();
        Range range = new Range();
        Timex3 oldTimex3 = null;
        Timex3 newTimex3;
        for (CoreMap cm : timexAnnsAll) {
            if (text.contains(cm.toString())) {
                String[] splits = text.split("[^A-Za-z0-9]*" + cm + "[^A-Za-z0-9]*", 2);
                text = removeTrailingPreposition(splits[0]) + " " + splits[1];
            }
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();
            Timex timex = cm.get(TimeAnnotations.TimexAnnotation.class);
            newTimex3 = Timex3.fromXml(timex.toString());
            logger.info("-----------------------------------------------------------------");
            logger.info("  temporal.getTimexValue(): " + temporal.getTimexValue());
            logger.info("  temporal.getDuration(): " + temporal.getDuration());
            logger.info("  temporal.getPeriod(): " + temporal.getPeriod());
            logger.info("  timex.text(): " + timex.text());
            logger.info("  timex.toString(): " + timex.toString());
            logger.info("  timex.value(): " + timex.value());
            logger.info("-----------------------------------------------------------------");

            range = mergeRanges(newTimex3, oldTimex3, range);
            oldTimex3 = newTimex3;
            System.out.println("-----$$$#$@$@$@$ range: " + range);
            edscTemporal.setTemporal(edscTemporal.getTemporal() + ", " + temporal.toString());
            edscTemporal.setTimex(edscTemporal.getTimex() + ", " + temporal.getTimexValue());
            edscTemporal.setTextAfterExtraction(text.replaceAll("[^A-Za-z0-9]*$", ""));
        }
        try {
            if (timexAnnsAll.size() > 0) {
                edscTemporal.setQuery(getEdscQuery(range));
                edscTemporal.setStart(range.getBegin() + "Z");
                edscTemporal.setEnd(range.getEnd() + "Z");
                edscTemporal.setRecurring(range.getPeriodicity() != -1);
            } else {
                return null;
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage(), e);
            edscTemporal.setQuery(null);
        }

        return edscTemporal;
    }

    private static String getEdscQuery(Range range) throws ParseException {
        if (range == null || range.getBegin() == null && range.getEnd() == null) {
            return null;
        }
        // only apply annual recurring
        int beginDayOfYear;
        int endDayOfYear;
        if (range.getPeriodicity() != -1) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Calendar beginCal = Calendar.getInstance();
            beginCal.setTime(formatter.parse(range.getBegin()));
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(formatter.parse(range.getEnd()));
            beginDayOfYear = beginCal.get(Calendar.DAY_OF_YEAR);
            endDayOfYear = endCal.get(Calendar.DAY_OF_YEAR);
            return range.getBegin() + "Z" + "," + range.getEnd() + "Z," + beginDayOfYear + "," + endDayOfYear;
        }
        return range.getBegin() + "Z" + "," + range.getEnd() + "Z";
    }

    private static Range mergeRanges(Timex3 timex3, Timex3 oldTimex3, Range oldRange) {
        if (timex3.getType().equals("SET")) {
            // CMR can only deal with annual recurring temporals
            if (Pattern.matches("P\\d+Y", timex3.getVal())) {
                oldRange.setPeriodicity(Integer.parseInt(timex3.getVal().substring(1, timex3.getVal().indexOf("Y"))));
            }
            return oldRange;
        } else {
            Range range = timex3.getRange();

            Calendar begin = toCalendar(range.getBegin(), oldRange.getBegin(), true);
            Calendar end = toCalendar(range.getEnd(), oldRange.getEnd(), false);

            if (begin != null && end != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                return new Range(formatter.format(begin.getTime()),
                        formatter.format(end.getTime()), "", oldRange.getPeriodicity());
            }
            return null;
        }
    }

    private static Calendar toCalendar(String rangeDateTime, String oldRangeDateTime, boolean isBegin) {
        rangeDateTime = rangeDateTime.toUpperCase(); // xxxx-xx-03 to XXXX-XX-03
        oldRangeDateTime = oldRangeDateTime.toUpperCase();

        String regex = "(?:XXXX|\\d{4,})-(?:XX|\\d{1,2})(-(?:XX|\\d{1,2})(-W(?:XX|\\d{1,2})-\\d)?(T\\d{1,2}:\\d{1,2}((:\\d{1,2})?(\\.\\d{1,3})?)?)?)?";
        if (rangeDateTime != null && !rangeDateTime.isEmpty() && Pattern.matches(regex, rangeDateTime)) {
            String[] datetime = rangeDateTime.split("T");
            String[] date = datetime[0].split("-");
            String[] time = datetime.length > 1 ? datetime[1].split(":") : null;

            int year, month, dayOfMonth, hour, minute, second;
            if (oldRangeDateTime != null && !oldRangeDateTime.isEmpty() && Pattern.matches(regex, oldRangeDateTime)) {
                String[] oldDatetime = oldRangeDateTime.split("T");
                String[] oldDate = oldDatetime[0].split("-");
                String[] oldTime = oldDatetime.length > 1 ? oldDatetime[1].split(":") : null;

                logger.info("------- " + rangeDateTime + ", " + oldRangeDateTime);
                // merge year
                if (date[0].equals("XXXX")) {
                    if (oldDate[0].equals("XXXX")) {
                        year = isBegin
                                ? Calendar.getInstance().getActualMinimum(Calendar.YEAR)
                                : Calendar.getInstance().getActualMaximum(Calendar.YEAR);
                    } else {
                        year = Integer.parseInt(oldDate[0]);
                    }
                } else if (oldDate[0].equals("XXXX")) {
                    year = Integer.parseInt(date[0]);
                } else {
                    int tmp1 = Integer.parseInt(date[0]);
                    int tmp2 = Integer.parseInt(oldDate[0]);
                    year = isBegin ? Math.max(tmp1, tmp2) : Math.min(tmp1, tmp2);
                }

                // merge month
                if (date[1].equals("XX")) {
                    month = oldDate[1].equals("XX") ? 1 : Integer.parseInt(oldDate[1]);
                } else if (oldDate[1].equals("XX")) {
                    month = Integer.parseInt(date[1]);
                } else {
                    int tmp1 = Integer.parseInt(date[1]);
                    int tmp2 = Integer.parseInt(oldDate[1]);
                    month = isBegin ? Math.max(tmp1, tmp2) : Math.min(tmp1, tmp2);
                }

                // merge day
                if (date.length < 3 && oldDate.length < 3) {
                    dayOfMonth = 1;
                } else if (date.length < 3 && oldDate.length >= 3) {
                    dayOfMonth = Integer.parseInt(oldDate[2]);
                } else if (date.length >= 3 && oldDate.length < 3) {
                    dayOfMonth = Integer.parseInt(date[2]);
                } else if (date[2].equals("XX")) {
                    dayOfMonth = oldDate[2].equals("XX") ? 1 : Integer.parseInt(oldDate[2]);
                } else if (oldDate[2].equals("XX")) {
                    dayOfMonth = Integer.parseInt(date[2]);
                } else {
                    int tmp1 = Integer.parseInt(date[2]);
                    int tmp2 = Integer.parseInt(oldDate[2]);
                    dayOfMonth = isBegin ? Math.max(tmp1, tmp2) : Math.min(tmp1, tmp2);
                }

                // merge time
                if (time != null) {
                    if (oldTime == null) {
                        hour = Integer.parseInt(time[0]);
                        minute = Integer.parseInt(time[1]);
                        second = Integer.parseInt(time[2]);
                    } else {
                        int tmp1 = Integer.parseInt(time[0]);
                        int tmp2 = Integer.parseInt(oldTime[0]);
                        hour = isBegin ? Math.max(tmp1, tmp2) : Math.min(tmp1, tmp2);

                        tmp1 = Integer.parseInt(time[1]);
                        tmp2 = Integer.parseInt(oldTime[1]);
                        minute = isBegin ? Math.max(tmp1, tmp2) : Math.min(tmp1, tmp2);

                        if (time.length < 3) {
                            if (oldTime.length < 3) {
                                second = 0;
                            } else {
                                second = Integer.parseInt(oldTime[2]);
                            }
                        } else if (oldTime.length < 3) {
                            second = Integer.parseInt(time[2]);
                        } else {
                            tmp1 = Integer.parseInt(time[2]);
                            tmp2 = Integer.parseInt(oldTime[2]);
                            second = isBegin ? Math.max(tmp1, tmp2) : Math.min(tmp1, tmp2);
                        }
                    }
                } else if (oldTime != null) {
                    hour = Integer.parseInt(oldTime[0]);
                    minute = Integer.parseInt(oldTime[1]);
                    second = Integer.parseInt(oldTime[2]);
                } else {
                    hour = minute = second = 0;
                }

                return makeCalendar(year, month, dayOfMonth, hour, minute, second);
            }

        }
        logger.error(String.format("Unknown value \"%s\"", rangeDateTime));
        return null;
    }

    private static Calendar makeCalendar(int year, int month, int day, int hour, int minute, int sec) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(year, month - 1, day, hour, minute, sec);
        return date;
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
