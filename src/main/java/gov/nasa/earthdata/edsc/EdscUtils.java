package gov.nasa.earthdata.edsc;

import com.bericotech.clavin.resolver.ResolvedLocation;
import com.bericotech.clavin.rest.core.ResolvedLocations;
import com.bericotech.clavin.rest.resource.ClavinRestResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;
import gov.nasa.earthdata.edsc.EdscPrepositions.Prepositions;
import gov.nasa.earthdata.edsc.spatial.EdscBoundingBox;
import gov.nasa.earthdata.edsc.spatial.EdscPoint;
import gov.nasa.earthdata.edsc.spatial.EdscSpatial;
import gov.nasa.earthdata.edsc.temporal.EdscTemporal;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
        logger.info("+_+_+_+_+_ text: " + text);       
        for (Prepositions prep : Prepositions.values()) {
            logger.info("----- prep: " + prep.value());
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
            logger.info("------- getMatchedName(): " + rLoc.getMatchedName());
            logger.info("------- getGeoname().toString(): " + rLoc.getGeoname().toString());
            logger.info("------- getConfidence(): " + rLoc.getConfidence());
            logger.info("------- getLocation().getText(): " + rLoc.getLocation().getText());
            geoName += delim + rLoc.getMatchedName();
            delim = ",";

            // Remove matched spatial (and prepositions) from text
            if (text.toLowerCase().contains(rLoc.getMatchedName().toLowerCase())) {
                logger.info("+++++ trailing preps detected: " + text);
                String[] splits = text.toLowerCase().split("[^A-Za-z0-9]*" + rLoc.getMatchedName().toLowerCase() + "[^A-Za-z0-9]*", 2);
                text = removeTrailingPreposition(splits[0]) + " " + splits[1];
                logger.info("+++++ trailing preps removed: " + text);
            }

            // Add country to the extracted geoname string
            if (rLoc.getGeoname().getPrimaryCountryName().equals(rLoc.getMatchedName())) {
                isCountry = true;
            }
            if (!isCountry && country.isEmpty() && !rLoc.getGeoname().getPrimaryCountryName().isEmpty()) {
                country = rLoc.getGeoname().getPrimaryCountryName();
                logger.info("-------- country: " + country);
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
                            EdscPoint nePoint = new EdscPoint(bbox.getDouble("north"), bbox.getDouble("east"));
                            EdscPoint swPoint = new EdscPoint(bbox.getDouble("south"), bbox.getDouble("west"));
                            EdscSpatial spatial = new EdscSpatial();
                            spatial.setBbox(new EdscBoundingBox(swPoint, nePoint));
                            spatial.setGeonames(geoName);
                            spatial.setTextAfterExtraction(text);
                            spatial.setQuery("sb=" + swPoint.getLongitude() + "%2C" + swPoint.getLatitude() + "%2C" + nePoint.getLongitude() + "%2C" + nePoint.getLatitude());
                            return spatial;
                        }
                    }
                }
            } else {
                logger.error("Request '" + uri + "' returned status code: " + response.getStatus());
                return null;
            }
        }
        
        logger.info ("No resolved locations found from text: " + text);
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
        for (CoreMap cm : timexAnnsAll) {
            if (text.contains(cm.toString())) {
                logger.info("+++++ before removing temporal: " + text);
                String[] splits = text.split("[^A-Za-z0-9]*" + cm + "[^A-Za-z0-9]*", 2);
                text = removeTrailingPreposition(splits[0]) + " " + splits[1];
                logger.info("+++++ after removing temporal: " + text);
            }
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();
            Timex timex = cm.get(TimeAnnotations.TimexAnnotation.class);
            edscTemporal.setTemporal(temporal.toString());
            edscTemporal.setTimex(temporal.getTimexValue());
            edscTemporal.setTextAfterExtraction(text.replaceAll("[^A-Za-z0-9]*$", ""));
            edscTemporal.setQuery(timexToQuery(temporal.getTimexValue()));
            
            logger.info("0----- temporal.getTimexValue(): " + temporal.getTimexValue());
            logger.info("0----- timex.text(): " + timex.text());
            logger.info("0----- timex.toString(): " + timex.toString());
            logger.info("0----- timex.value(): " + timex.value());
            logger.info("0----- timex.getRange().first(): " + timex.getRange().first().getTime());
            logger.info("0----- timex.getRange().second(): " + timex.getRange().second().getTime());
            
            Timex timex2 = new Timex("DATE", "2015SU");
            logger.info("0----- timex2.text(): " + timex2.text());
            logger.info("0----- timex2.toString(): " + timex2.toString());
            logger.info("0----- timex2.value(): " + timex2.value());
            logger.info("0----- timex2.getRange().first(): " + timex2.getRange().first().getTime());
            logger.info("0----- timex2.getRange().second(): " + timex2.getRange().second().getTime());
        }
        
        return edscTemporal;
    }

    private static String timexToQuery(String timexValue) {
        
        return null;
    }
}
