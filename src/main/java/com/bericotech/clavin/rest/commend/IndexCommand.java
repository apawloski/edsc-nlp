package com.bericotech.clavin.rest.commend;

import com.bericotech.clavin.index.IndexDirectoryBuilder;
import gov.nasa.earthdata.edsc.nlp.rest.NLPRestConfiguration;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import com.yammer.dropwizard.config.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;



public class IndexCommand extends ConfiguredCommand<NLPRestConfiguration> {


    public IndexCommand() {
        super("index", "Index a geonames.org gazetteer");
    }


    @Override
    protected void run(Bootstrap<NLPRestConfiguration> bootstrap,
                       Namespace namespace,
                       NLPRestConfiguration configuration) throws Exception {

        // send empty arguments for now
        String[] args = new String[1];
        args[0] = "";

        IndexDirectoryBuilder.main(args);

    }

}

