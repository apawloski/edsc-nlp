CLAVIN Rest
===========

## Quick Start

### Clone the project

    git clone git@github.com:mightynimble/clavin-rest.git
    cd clavin-rest

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

    cp target/clavin-rest-0.3.0-SNAPSHOT.jar ./demo/
    cp clavin-rest.yml ./demo/

### Create a CLAVIN gazetteer

    cd demo
    java -Xmx4096m -jar clavin-rest-0.3.0-SNAPSHOT.jar index clavin-rest.yml

### Run the CLAVIN rest server

    java -Xmx2048m -jar clavin-rest-0.3.0-SNAPSHOT.jar server clavin-rest.yml

### Geotag a string

    curl -s --data "Norway is a small town in Maine" --header "Content-Type: text/plain" http://localhost:9090/api/v0/geotag

### Temporal extraction
    curl -s --data "Dump trash on Thursdays" --header "Content-Type: text/plain" http://localhost:9090/api/v0/temporal

### Try in browser:

    http://localhost:9090
