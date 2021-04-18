FROM azul/zulu-openjdk:11

ENV APP_DIR /app
ENV PORT 8080
ENV HTTP_AUTH_USER user
ENV HTTP_AUTH_PASSWORD password
ENV COMMAND_TIMEOUT 1200

RUN mkdir $APP_DIR
WORKDIR $APP_DIR

COPY target/agent-4.2.2-SNAPSHOT-exec.jar $APP_DIR/agent-exec.jar
ADD https://estuary-agent-go.s3.eu-central-1.amazonaws.com/4.1.0/runcmd-linux $APP_DIR/runcmd
COPY https $APP_DIR/https

RUN chmod +x $APP_DIR/runcmd

ENTRYPOINT ["java", "-jar", "/app/agent-exec.jar"]
