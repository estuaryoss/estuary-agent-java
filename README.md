# About
Agent written in Java (SpringBoot) as part of **estuary** stack. 

The advantage of this implementation is that java libraries can be integrated within, rather than executing the logic through cli commands pointing to a main class in a jar, as per the original python implementation.

## Artifact
![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/com.github.dinuta.estuary/testrunner/4.0.6)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.dinuta.estuary/testrunner?server=https%3A%2F%2Foss.sonatype.org)

## Build status
[![CircleCI](https://circleci.com/gh/dinuta/estuary-agent-java.svg?style=svg&circle-token=2036f4d0e07fadce8101e00e790970fcfb43e03f)](https://circleci.com/gh/dinuta/estuary-agent-java)

## Code quality
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/cbdcf91a317e4c7ba19b49a9a7c6fd42)](https://www.codacy.com/manual/dinuta/estuary-agent-java?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dinuta/estuary-agent-java&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/d5f0b9e8d3c948b8f56b/maintainability)](https://codeclimate.com/github/dinuta/estuary-agent-java/maintainability)

## Eureka client registration
Set the following env vars:  
-   APP_IP -> the ip which this service binds to
-   PORT  -> the port which this service binds to

## Fluentd logging
-   FLUENTD_IP_PORT  -> This env var sets the fluentd ip:port connection. Example: localhost:24224  

## Token Authentication
-   HTTP_AUTH_TOKEN -> This env var sets the auth token for the service. Will be matched with the header 'Token'

## Command timeout
-   COMMAND_TIMEOUT -> This env var sets the command timeout for the system commands. Default is **1800** seconds.  

## More information
This service acts with small differences as the original [python implementation](https://github.com/dinuta/estuary-agent).  
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
    <version>4.0.6</version>
</dependency>
```
## Maven devendency snapshot
```xml
<dependency>
    <groupId>com.github.dinuta.estuary</groupId>
    <artifactId>agent</artifactId>
    <version>4.0.8-SNAPSHOT</version>
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
