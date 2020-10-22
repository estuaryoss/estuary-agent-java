FROM azul/zulu-openjdk:11

ENV APP_DIR /app
ENV PORT 8080
ENV HTTP_AUTH_TOKEN None
ENV COMMAND_TIMEOUT 1200

RUN mkdir $APP_DIR
WORKDIR $APP_DIR

COPY target/agent-4.2.0-SNAPSHOT-exec.jar $APP_DIR
ADD https://estuary-agent.s3.eu-central-1.amazonaws.com/4.2.0/start.py-linux $APP_DIR/start.py
COPY https $APP_DIR/https

RUN chmod +x $APP_DIR/start.py

ENTRYPOINT ["java", "-jar", "/app/agent-4.2.0-SNAPSHOT-exec.jar"]
