FROM azul/zulu-openjdk:11

ENV APP_DIR /app
ENV PORT 8080
ENV HTTP_AUTH_USER user
ENV HTTP_AUTH_PASSWORD password
ENV COMMAND_TIMEOUT 1200

RUN mkdir $APP_DIR
WORKDIR $APP_DIR

COPY target/agent-4.2.4-SNAPSHOT-exec.jar $APP_DIR/agent-exec.jar
COPY https $APP_DIR/https

ENTRYPOINT ["java", "-jar", "/app/agent-exec.jar"]
