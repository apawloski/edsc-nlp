package com.bericotech.clavin.rest;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.gazetteer.query.Gazetteer;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import com.bericotech.clavin.rest.command.IndexCommand;
import org.apache.lucene.queryparser.classic.ParseException;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.nerd.StanfordExtractor;
import com.bericotech.clavin.rest.resource.ClavinRestResource;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.util.CoreMap;



public class ClavinRestService extends Service<ClavinRestConfiguration> {

    public static void main(String[] args) throws Exception {
        new ClavinRestService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ClavinRestConfiguration> bootstrap) {
        bootstrap.setName("clavin-rest");
        //bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/"));
        bootstrap.addCommand(new IndexCommand());
    }

    @Override
    public void run(ClavinRestConfiguration configuration,
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
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));

        environment.addResource(new ClavinRestResource(parser, pipeline));
    }

}
