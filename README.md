Earthdata Search NLP (Natural Language Processor)
===========

An opensource project based on CLAVIN and StanfordNLP to help Earthdata Search parse spatial and temporal fields out of plain text.

## Quick Start

### Clone the project

    git clone git@github.com:mightynimble/edsc-nlp.git
    cd edsc-nlp

### Download StanfordNLP to libs directory

    cd libs
    curl -O http://nlp.stanford.edu/software/stanford-corenlp-full-2015-12-09.zip
    unzip stanford-corenlp-full-2015-12-09.zip
    cd ../

### Download Geonames to demo directory

    cd demo
    curl -O http://download.geonames.org/export/dump/allCountries.zip
    unzip allCountries.zip
    cp ../src/main/resources/SupplementaryGazetteer.txt .
    cd ../

### Package creation

    mvn package

### Copy jar and config file to demo directory

    cp target/edsc-nlp-0.1.jar ./demo/
    cp edsc-nlp.yml ./demo/

### Create a CLAVIN gazetteer

    cd demo
    java -Xmx4096m -jar edsc-nlp-0.1.jar index edsc-nlp.yml

### Run the rest server

    java -Xmx2048m -jar edsc-nlp-0.1.jar server edsc-nlp.yml

### Extract spatial and temporal fields from some string

    curl -i http://localhost:15400/nlp?text=Norway%20is%20a%20small%20town%20in%20Maine
    curl -i http://localhost:15400/nlp?text=Snow%20cover%20in%Boston%20last%20winter
