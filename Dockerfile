FROM azul/zulu-openjdk:11

ENV APP_DIR /app
ENV PORT 8080
ENV COMMAND_TIMEOUT 1200

RUN mkdir $APP_DIR
WORKDIR $APP_DIR

COPY target/agent*exec.jar $APP_DIR/agent-exec.jar
COPY https $APP_DIR/https

ENTRYPOINT ["java", "-jar", "/app/agent-exec.jar"]
