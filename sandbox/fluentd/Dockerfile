FROM fluentd:v1.9.1-debian-1.0
USER root

RUN ["fluent-gem", "install", "fluent-plugin-elasticsearch", "--no-document", "--version", "4.1.1"]
RUN ["fluent-gem", "install", "fluent-plugin-gelf"]

USER fluent 