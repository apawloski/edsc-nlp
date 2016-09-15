package gov.nasa.earthdata.edsc.nlp.rest;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.gazetteer.query.Gazetteer;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import com.bericotech.clavin.rest.commend.IndexCommand;
import org.apache.lucene.queryparser.classic.ParseException;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.nerd.StanfordExtractor;
import gov.nasa.earthdata.edsc.nlp.rest.resource.NLPResource;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NLPRestService extends Service<NLPRestConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(NLPRestService.class);

    public static void main(String[] args) throws Exception {
        new NLPRestService().run(args);
    }

    @Override
    public void initialize(Bootstrap<NLPRestConfiguration> bootstrap) {
        bootstrap.setName("edsc-nlp");
        bootstrap.addCommand(new IndexCommand());
    }

    @Override
    public void run(NLPRestConfiguration configuration,
            Environment environment) throws ClassCastException, ClassNotFoundException, IOException, ParseException, ClavinException {
        final String luceneDir = configuration.getLuceneDir();
        final Integer maxHitDepth = configuration.getMaxHitDepth();
        final Integer maxContextWindow = configuration.getMaxContextWindow();
        final Boolean fuzzy = configuration.getFuzzy();
        final Boolean suTimeIncludeRange = configuration.getSuTimeIncludeRange();
        final Boolean suTimeMarkTimeRanges = configuration.getSuTimeMarkTimeRanges();
        final String geoNamesUrl = configuration.getGeoNamesUrl();
        final String geoNamesUserId = configuration.getGeoNamesUserId();
        logger.info("----------------------CONFIGURATIONS---------------------");
        logger.info("luceneDir: " + luceneDir);
        logger.info("maxHitDepth: " + maxHitDepth);
        logger.info("maxContextWindow: " + maxContextWindow);
        logger.info("fuzzy: " + fuzzy);
        logger.info("suTimeIncludeRange: " + suTimeIncludeRange);
        logger.info("suTimeMarkTimeRanges: " + suTimeMarkTimeRanges);
        logger.info("geoNamesUrl: " + geoNamesUrl);
        logger.info("geoNamesUserId: " + geoNamesUserId);
        logger.info("--------------------------------------------------------");

        Gazetteer gazetteer = new LuceneGazetteer(new File(luceneDir));

        StanfordExtractor extractor = new StanfordExtractor();
        GeoParser parser = new GeoParser(extractor, gazetteer, maxHitDepth, maxContextWindow, fuzzy);

        Properties props = new Properties();
        props.setProperty("sutime.includeRange", suTimeIncludeRange.toString());
//        props.setProperty("sutime.includeNested", "true");
        props.setProperty("sutime.markTimeRanges", suTimeMarkTimeRanges.toString());

        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));

        String url = geoNamesUrl + "?username=" + geoNamesUserId + "&type=json&maxRows=1&isNameRequired=true&style=full&q=";
        environment.addResource(new NLPResource(parser, pipeline, url));
    }
}
