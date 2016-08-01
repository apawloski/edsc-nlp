package com.bericotech.clavin.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.bericotech.clavin.rest.core.ResolvedLocations;
import com.bericotech.clavin.rest.core.ResolvedLocationsMinimum;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.util.CoreMap;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import java.net.URLEncoder;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/v0")
@Produces(MediaType.APPLICATION_JSON)
public class ClavinRestResource {
    private static final Logger logger = LoggerFactory.getLogger(ClavinRestResource.class);

    private final GeoParser parser;
    private final AnnotationPipeline pipeline;

    public ClavinRestResource(GeoParser parser, AnnotationPipeline pipeline) {
        this.parser = parser;
        this.pipeline = pipeline;
    }

    @GET
    public String index() {
        return "CLAVIN-rest 0.1";
    }

    @POST
    @Path("/geotag")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response extractAndResolveSimpleLocationsFromText(String text) {

        ResolvedLocations result = null;
        try {
            List<ResolvedLocation> resolvedLocations = parser.parse(text);
            result = new ResolvedLocations(resolvedLocations);
            
            String geoName = "";
            String delim = "";
            String country = "";
            for (ResolvedLocation rLoc : resolvedLocations) {
                logger.info("------- getMatchedName(): " + rLoc.getMatchedName());
                logger.info("------- getGeoname().toString(): " + rLoc.getGeoname().toString());
                logger.info("------- getConfidence(): " + rLoc.getConfidence());
                logger.info("------- getLocation().getText(): " + rLoc.getLocation().getText());
                geoName += delim + rLoc.getMatchedName();
                delim = ",";
                if (country.isEmpty() && !rLoc.getGeoname().getPrimaryCountryName().isEmpty()) {
                    country = rLoc.getGeoname().getPrimaryCountryName();
                    logger.info("-------- countrY: " + country);
                }
            }
            if (!country.isEmpty()) {
                geoName += "," + country;
            }
            
            if (resolvedLocations.size() > 0) {
                String uri = "http://api.geonames.org/search?username=edsc&type=json&maxRows=1&isNameRequired=true&style=full&q=" + URLEncoder.encode(geoName, "UTF-8");
                logger.info("Seding request to geonames.org: " + uri);
                ClientConfig clientConfig = new DefaultClientConfig();
                clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
                
                Client client = Client.create(clientConfig);
                WebResource resource = client.resource(uri);
                ClientResponse response = resource.accept("application/json").get(ClientResponse.class);;
                
                if (response.getStatus() == Status.OK.getStatusCode()) {
                    return Response.status(200).entity(response.getEntity(String.class)).build();
                }
                else {
                    logger.error("Request '" + uri + "' returned status code: " + response.getStatus());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e).build();
        }

        return Response.status(200).entity(result).build();
    }

    @POST
    @Path("/geotagmin")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response extractAndResolveSimpleShortLocationsFromText(String text) {

        ResolvedLocationsMinimum result = null;
        try {
            List<ResolvedLocation> resolvedLocations = parser.parse(text);
            result = new ResolvedLocationsMinimum(resolvedLocations);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e).build();
        }

        return Response.status(200).entity(result).build();
    }

    @POST
    @Path("/temporal")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response extractTemporalFromText(String text) {
        Annotation annotation = new Annotation(text);
        annotation.set(CoreAnnotations.DocDateAnnotation.class,
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        pipeline.annotate(annotation);
        System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
        String out = "";
        for (CoreMap cm : timexAnnsAll) {
            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();
            out += cm + " --> " + temporal + "\n";
            out += "getRange: " + temporal.getRange() + "\n";
            out += "getTimeLabel: " + temporal.getTimeLabel() + "\n";
            out += "getTimexValue: " + temporal.getTimexValue() + "\n";
            out += "toISOString: " + temporal.toISOString() + "\n";
            out += "getDuration: " + temporal.getDuration() + "\n";
            out += "getGranularity: " + temporal.getGranularity() + "\n";
            out += "getPeriod: " + temporal.getPeriod()+ "\n";
            out += "getStandardTemporalType: " + temporal.getStandardTemporalType() + "\n";
            out += "getTime: " + temporal.getTime() + "\n";
            out += "getTimexType: " + temporal.getTimexType()+ "\n";
            out += "getUncertaintyGranularity: " + temporal.getUncertaintyGranularity() + "\n";
            out += "getMod: " + temporal.getMod() + "\n";
            out += "toString" + temporal.toString() + "\n\n";
        }
        // System.out.println("--");
        return Response.status(200).entity(out).build();
    }
}
