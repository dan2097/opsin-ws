# OPSIN WS

### Usage
* Checkout the repository
* Download [Maven](https://maven.apache.org/) if this is not already present on your machine


To run directly as a standalone web-service, from a command-line in the opsin-ws folder run:
```
mvn jetty:run
```

Then browse to 127.0.0.1:8989
```
firefox 127.0.0.1:8989
```

To run on a Java Web server e.g. [Apache Tomcat](https://tomcat.apache.org/), from a command-line in the opsin-ws folder run:
```
mvn package
```
This will create opsin.war in the target folder, which can (in the case of Tomcat) then be copied into the webapps folder of the Java Web Server to deploy it

![Build Status](https://github.com/dan2097/opsin-ws/workflows/Java%20CI%20with%20Maven/badge.svg)
