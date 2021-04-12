FROM registry.access.redhat.com/ubi8/openjdk-11:latest
EXPOSE 8080
USER 1001
ARG var_kafka_bootstrap_servers
ENV APP_KAFKA_BOOTSTRAP_SERVERS=$var_kafka_bootstrap_servers
ARG var_db_url
ENV APP_DB_URL=$var_db_url
COPY build/libs/krm-save-send-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java"]
CMD ["-Xms64m", "-Xmx512m", "-jar", "/app/app.jar"]