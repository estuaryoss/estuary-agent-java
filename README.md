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

Example:  
 ```bash
export APP_IP=192.168.0.4
export PORT=8081
java -jar \
-Deureka.client.serviceUrl.defaultZone=http://192.168.0.100:8080/eureka/v2 \
-Deureka.client.enabled=true agent-4.0.8-SNAPSHOT-exec.jar 
```

## Fluentd logging
-   FLUENTD_IP_PORT  -> This env var sets the fluentd ip:port connection. Example: localhost:24224  

## Token Authentication
-   HTTP_AUTH_TOKEN -> This env var sets the auth token for the service. Will be matched with the header 'Token'

## Command timeout
-   COMMAND_TIMEOUT -> This env var sets the command timeout for the system commands. Default is **1800** seconds.  

## More information
This service acts with small differences as the original [python implementation](https://github.com/dinuta/estuary-agent).  
All the documentation should be matched, minus some differences in terms how this service registers to eureka.

## Example output
curl -X POST -d 'ls -lrt' http://localhost:8080/command

```json
{
    "code": 1000,
    "message": "Success",
    "description": {
        "finished": true,
        "started": false,
        "startedat": "2020-08-15 19:38:16.138962",
        "finishedat": "2020-08-15 19:38:16.151067",
        "duration": 0.012,
        "pid": 2315,
        "id": "none",
        "commands": {
            "ls -lrt": {
                "status": "finished",
                "details": {
                    "out": "total 371436\n-rwxr-xr-x 1 dinuta qa  13258464 Jun 24 09:25 main-linux\ndrwxr-xr-x 4 dinuta qa        40 Jul  1 11:42 tmp\n-rw-r--r-- 1 dinuta qa  77707265 Jul 25 19:38 testrunner-linux.zip\n-rw-r--r-- 1 dinuta qa 106655730 Jul 27 09:05 testrunner-1.2-20200727.090514-12-exec.jar\n-rw-r--r-- 1 dinuta qa  59349111 Jul 30 08:03 ats-ops.jar\n-rw-r--r-- 1 dinuta qa     11348 Jul 30 08:03 LICENSE\n-rwxr-xr-x 1 dinuta qa  11635504 Jul 30 08:03 start.py\n-rw-r--r-- 1 dinuta qa 106811336 Aug 14 07:42 agent-exec.jar\n-rw------- 1 dinuta qa   4911271 Aug 14 10:00 nohup.out\n",
                    "err": "",
                    "code": 0,
                    "pid": 6803,
                    "args": [
                        "/bin/sh",
                        "-c",
                        "ls -lrt"
                    ]
                },
                "startedat": "2020-08-15 19:38:16.138970",
                "finishedat": "2020-08-15 19:38:16.150976",
                "duration": 0.012
            }
        }
    },
    "time": "2020-08-15 19:38:16.151113",
    "name": "estuary-agent",
    "version": "4.0.8"
}
```

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
