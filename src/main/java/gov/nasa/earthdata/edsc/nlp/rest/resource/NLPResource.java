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
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces(MediaType.APPLICATION_JSON)
public class NLPResource {

    private static final Logger logger = LoggerFactory.getLogger(NLPResource.class);

    private final GeoParser parser;
    private final AnnotationPipeline pipeline;

    public NLPResource(GeoParser parser, AnnotationPipeline pipeline) {
        this.parser = parser;
        this.pipeline = pipeline;
    }

    @GET
    @Path("/nlp")
    @Produces(MediaType.APPLICATION_JSON)
    public Response contextParsing(@QueryParam("text") String text) {
        /*
        * spatial extraction
         */
        EdscSpatial edscSpatial;
        try {
            edscSpatial = EdscUtils.spatialParsing(text, parser.parse(text));
        } catch (Exception e) {
            return Response.status(500).entity(e).build();
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

        return Response.status(200).entity(edscResponse).build();
    }

}
