package gov.nasa.earthdata.edsc.nlp.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bericotech.clavin.GeoParser;
import edu.stanford.nlp.pipeline.*;
import gov.nasa.earthdata.edsc.nlp.utils.EdscResponse;
import gov.nasa.earthdata.edsc.nlp.utils.EdscUtils;
import gov.nasa.earthdata.edsc.nlp.spatial.EdscSpatial;
import gov.nasa.earthdata.edsc.nlp.temporal.EdscTemporal;
import java.time.Duration;
import java.time.Instant;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class NLPResource {

    private static final Logger logger = LoggerFactory.getLogger(NLPResource.class);

    private final GeoParser parser;
    private final AnnotationPipeline pipeline;
    private final String geoNamesUrl;

    public NLPResource(GeoParser parser, AnnotationPipeline pipeline, String geoNamesUrl) {
        this.parser = parser;
        this.pipeline = pipeline;
        this.geoNamesUrl = geoNamesUrl;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sanityCheck() {
        return Response.status(200).entity("OK").build();
    }

    @GET
    @Path("nlp")
    @Produces(MediaType.APPLICATION_JSON)
    public Response contextParsing(@QueryParam("text") String text) {
        Instant start = Instant.now();
        logger.info("Start to process text: '" + text + "'");
        /*
        * spatial extraction
         */
        EdscSpatial edscSpatial;
        try {
            // CLAVIN can't recognize place names in all lowercase. We need to capitalize the text string
            text = WordUtils.capitalizeFully(text);
            edscSpatial = EdscUtils.spatialParsing(text, parser.parse(text), geoNamesUrl);
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getLocalizedMessage(), e);
            edscSpatial = null;
        }

        /*
        * temporal extraction
         */
        if (edscSpatial != null) {
            text = edscSpatial.getTextAfterExtraction();
        }
        EdscTemporal edscTemporal = EdscUtils.temporalParsing(text, pipeline);

        EdscResponse edscResponse = new EdscResponse();
        edscResponse.setEdscSpatial(edscSpatial);
        edscResponse.setEdscTemporal(edscTemporal);
        if (edscTemporal != null) {
            edscResponse.setKeyword(edscTemporal.getTextAfterExtraction());
        } else {
            edscResponse.setKeyword(text);
        }
        Instant end = Instant.now();
        logger.info("Completed 200 OK in " + Duration.between(start, end).toMillis() + "ms.");
        return Response.status(200).entity(edscResponse).build();
    }

}
