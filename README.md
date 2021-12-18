<h1 align="center"><img src="./docs/images/banner_agent.png" alt="Estuary Agent"></h1>  

# About

The agent is written in Java (SpringBoot), and it executes low-level commands.

It enables any use case which implies system commands:

- Controlling and configuring the machines (via REST API)
- Exposing CLI applications via REST API
- Testing support by enabling SUT control and automation framework control
- IoT
- Home control integrations

It supports command execution having several modes:

- Commands executed sequentially
- Commands executed in parallel
- Commands executed in background
- Commands executed synchronously

With the help of the agent the user can also do IO operations:

- File upload and download (binary / text)
- Folder download (as zip archive)

This code acts both as a microservice as well as a library:

a) Standalone microservice jar with the
extension: [exec.jar](https://search.maven.org/artifact/com.github.estuaryoss/agent/4.2.0/jar)

```bash
java -jar agent-4.2.2-exec.jar
```

b) Library as a Maven dependency:

```xml

<dependency>
    <groupId>com.github.estuaryoss</groupId>
    <artifactId>agent</artifactId>
    <version>4.2.2</version>
</dependency>
```

## Artifact

![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/com.github.estuaryoss/agent/4.2.2)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.estuaryoss/agent?server=https%3A%2F%2Fs01.oss.sonatype.org)

## Build status

[![CircleCI](https://circleci.com/gh/estuaryoss/estuary-agent-java.svg?style=svg&circle-token=2036f4d0e07fadce8101e00e790970fcfb43e03f)](https://circleci.com/gh/estuaryoss/estuary-agent-java)

## Code quality

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/20bec8d5bf1b4197b6447b9f926c32ad)](https://www.codacy.com/gh/estuaryoss/estuary-agent-java/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=estuaryoss/estuary-agent-java&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/cb9958e3b834d93cb082/maintainability)](https://codeclimate.com/repos/5f6783d35aa6290178006578/maintainability)

## Postman collection

The postman collection is saved in folder **docs**

## Commands in background

Send your command using the classic endpoints, and then timeout from client.   
Use **/commands**, **/commands/running** and **/commands/finished** GET to retrieve your command information.

## Eureka client registration

Set the following env vars:

- APP_IP -> the ip which this service binds to
- PORT -> the port which this service binds to

Example:

 ```bash
export APP_IP=192.168.0.4
export PORT=8081
java -jar \
-Deureka.client.serviceUrl.defaultZone=http://192.168.0.100:8080/eureka/v2 \
-Deureka.client.enabled=true agent-4.2.3-SNAPSHOT-exec.jar 
```

## Fluentd logging

- FLUENTD_IP_PORT -> This env var sets the fluentd ip:port connection. Example: localhost:24224

## Authentication

### Method 1 - Spring security

- HTTP_AUTH_USER
- HTTP_AUTH_PASSWORD

These env vars will be matched against basic authentication from your HttpClient.  
After user auth, set the received cookie (JSESSIONID) to communicate further with the agent.  
The same settings can be set through application properties: **app.user** & **app.password**.  
The env vars precedence is higher than the one set through the application properties.

[!!!]() Use these env variables or swap application.properties file if you use it as dependency, otherwise you will open
a major security hole. The attacker will have access to your system. [!!!]()

### Method 2 - Token auth - No spring-security

- HTTP_AUTH_TOKEN -> This env var sets the auth token for the service. Will be matched with the header 'Token'  
  Note: The profile to be used is 'test'.

## Command timeout

- COMMAND_TIMEOUT -> This env var sets the command timeout for the system commands. Default is **1800** seconds.

## Enable HTTPS

Set **HTTPS_ENABLE** env var option to *true* or *false*.    
Set the certificate path (is relative!) with **HTTPS_KEYSTORE** and **HTTPS_KEYSTORE_PASSWORD** env variables. E.g.
HTTPS_KEYSTORE=file:https/keystore.p12  
If you do not set cert and keystore password env vars, it uses the ones from default *application.properties* in the
resource folder.

! Please also change the app port by setting the env var called **PORT** to *8443*. Default is 8080.

## Environment variables injection

User defined environment variables will be stored in a 'virtual' environment. The extra env vars will be used by the
process that executes system commands.  
There are two ways to inject user defined environment variables.

- call POST on **/env** endpoint. The body will contain the env vars in JSON format. E.g. {"FOO1":"BAR1"}
- create an **environment.properties** file with the extra env vars needed and place it in the same path as the JAR.
  Example in this repo.

*! All environment variables described above can also be set using **environment.properties**. However, the vars set
through **application.yml** can't be set: PORT, APP_IP, EUREKA_SERVER.*

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
          "out": "total 371436\n-rwxr-xr-x 1 dinuta qa  13258464 Jun 24 09:25 main-linux\ndrwxr-xr-x 4 dinuta qa        40 Jul  1 11:42 tmp\n-rw-r--r-- 1 dinuta qa  77707265 Jul 25 19:38 testrunner-linux.zip\n-rw------- 1 dinuta qa   4911271 Aug 14 10:00 nohup.out\n",
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
  "timestamp": "2020-08-15 19:38:16.151113",
  "path": "/command?",
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
  <groupId>com.github.estuaryoss</groupId>
  <artifactId>agent</artifactId>
  <version>4.0.8</version>
</dependency>
```

## Maven devendency snapshot

```xml

<dependency>
    <groupId>com.github.estuaryoss</groupId>
    <artifactId>agent</artifactId>
    <version>4.2.3-SNAPSHOT</version>
</dependency>
```

To use a snapshot version, set the s01.oss.sonatype.org repo in settings.xml:

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
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
</repository>
```

To generate Query classes needed for the DB queries run:  
mvn clean install (-DskipTests=true for faster compilation)

Support
project: <a href="https://paypal.me/catalindinuta?locale.x=en_US"><img src="https://lh3.googleusercontent.com/Y2_nyEd0zJftXnlhQrWoweEvAy4RzbpDah_65JGQDKo9zCcBxHVpajYgXWFZcXdKS_o=s180-rw" height="40" width="40" align="center"></a>   
