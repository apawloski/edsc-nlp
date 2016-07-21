CLAVIN Rest
===========

## Quick Start

### Download StanfordNLP

    http://stanfordnlp.github.io/CoreNLP/

### Package creation

    git clone git@github.com:mightynimble/clavin-rest.git
    cd clavin-rest
    mvn package

### Download Geonames

    curl -O http://download.geonames.org/export/dump/allCountries.zip

### Unzip Geonames

    unzip allCountries.zip

### Create a CLAVIN gazetteer

    java -Xmx4096m -jar ./target/clavin-rest-0.3.0-SNAPSHOT.jar index clavin-rest.yml

### Run the CLAVIN rest server

    java -Xmx2048m -jar ./target/clavin-rest-0.3.0-SNAPSHOT.jar server clavin-rest.yml

### Geotag a string

    curl -s --data "Norway is a small town in Maine" --header "Content-Type: text/plain" http://localhost:9090/api/v0/geotag

### Temporal extraction
    curl -s --data "Dump trash on Thursdays" --header "Content-Type: text/plain" http://localhost:9090/api/v0/temporal
