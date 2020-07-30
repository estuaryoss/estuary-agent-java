FROM azul/zulu-openjdk:11

ENV APP_DIR /app
ENV PORT 8080
ENV HTTP_AUTH_TOKEN None

RUN mkdir $APP_DIR
WORKDIR $APP_DIR

COPY target/testrunner-4.0.7-SNAPSHOT-exec.jar $APP_DIR

CMD ["java", "-jar", "/app/testrunner-4.0.7-SNAPSHOT-exec.jar"]
