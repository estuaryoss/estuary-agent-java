# About
estuary-testrunner written in Java (SpringBoot). 

The advantage of this implementation is that java libraries can be integrated within, rather than executing the logic through cli commands pointing to a main class in a jar, as per the original python implementation.

## Artifact
![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/com.github.dinuta.estuary/testrunner/4.0.6)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.dinuta.estuary/testrunner?server=https%3A%2F%2Foss.sonatype.org)

## Build status
[![CircleCI](https://circleci.com/gh/dinuta/estuary-testrunner-java.svg?style=svg&circle-token=2036f4d0e07fadce8101e00e790970fcfb43e03f)](https://circleci.com/gh/dinuta/estuary-testrunner-java)

## Code quality
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/dfbb5fd3b7cb4055a71d0f0b886917e3)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dinuta/estuary-testrunner-java&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/13925efd81d683c97123/maintainability)](https://codeclimate.com/github/dinuta/estuary-testrunner-java/maintainability)

## Eureka client registration
Set the following env vars:  
-  APP_IP -> the ip which this service binds to
-  PORT  -> the port which this service binds to


## Fluentd logging
-  FLUENTD_IP_PORT  -> the fluentd ip:port. Example: localhost:24224  

## Token Authentication
-  HTTP_AUTH_TOKEN -> the auth token for the service. Will be matched with the header 'Token'

## More information
This service acts with small differences as the original [python implementation](https://github.com/dinuta/estuary-testrunner).  
All the documentation should be matched, minus some differences in terms how this service registers to eureka.


## Overview  
The underlying library integrating swagger to SpringBoot is [springfox](https://github.com/springfox/springfox)  

Start your server as an simple java application  

You can view the api documentation in swagger-ui by pointing to  
http://localhost:8080/  

Change default port value in application.properties


## Maven dependency && settings.xml 
Get this dependency:
```xml
<dependency>
    <groupId>com.github.dinuta.estuary</groupId>
    <artifactId>testrunner</artifactId>
    <version>4.0.5</version>
</dependency>
```

For using a snapshot version, set the oss.sonatype.org repo in settings.xml:
```xml
<repository>
    <id>snaphosts4</id>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
    <releases>
        <enabled>false</enabled>
        <updatePolicy>always</updatePolicy>
    </releases>
    <name>all-external8</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
</repository>
```
