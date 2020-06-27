# About
estuary-testrunner written in Java (SpringBoot). 

The advantage of this implementation is that java libraries can be integrated within, rather than executing the logic through cli commands pointing to a main class in a jar, as per the original python implementation.

## Build status
[![CircleCI](https://circleci.com/gh/dinuta/estuary-testrunner-java.svg?style=svg&circle-token=2036f4d0e07fadce8101e00e790970fcfb43e03f)](https://circleci.com/gh/dinuta/estuary-testrunner-java)

## Code quality
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/dfbb5fd3b7cb4055a71d0f0b886917e3)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dinuta/estuary-testrunner-java&amp;utm_campaign=Badge_Grade)

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
