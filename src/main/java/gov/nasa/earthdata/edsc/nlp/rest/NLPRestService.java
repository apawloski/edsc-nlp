package gov.nasa.earthdata.edsc.nlp.rest;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.gazetteer.query.Gazetteer;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import gov.nasa.earthdata.edsc.nlp.rest.command.IndexCommand;
import org.apache.lucene.queryparser.classic.ParseException;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.nerd.StanfordExtractor;
import gov.nasa.earthdata.edsc.nlp.rest.resource.NLPResource;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;

public class NLPRestService extends Service<NLPRestConfiguration> {

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

        Gazetteer gazetteer = new LuceneGazetteer(new File(luceneDir));

        StanfordExtractor extractor = new StanfordExtractor();
        GeoParser parser = new GeoParser(extractor, gazetteer, maxHitDepth, maxContextWindow, fuzzy);
        // GeoParser parser = GeoParserFactory.getDefault("./IndexDirectory");

        Properties props = new Properties();
        props.setProperty("sutime.includeRange", "true");
//        props.setProperty("sutime.includeNested", "true");
        props.setProperty("sutime.markTimeRanges", "true");

        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));

        environment.addResource(new NLPResource(parser, pipeline));
    }
}
