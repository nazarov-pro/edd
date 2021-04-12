export DB_URL=jdbc:postgresql://127.0.0.1:5432/edd?user=edd&password=secret
export DB_URL_INTERNAL=jdbc:postgresql://db:5432/edd?user=edd&password=secret
export DB_SCHEMA=public
export DB_ROLLBACK_VERSION=version_1.0
export KAFKA_BOOTSTRAP_SERVERS=kafka:9092

docker-setup-network:
	@docker network create -d bridge edd-backend-net

## General Scope
set-up: docker-setup-network

## KRM SCOPE
krm-db-migration:
	@docker-compose -f ./data/docker-compose/postgres.yaml up -d
	@echo "Waiting 2 seconds..." && sleep 2
	@./gradlew :krm-debezium:update

krm-db-rollback-migration:
	@docker-compose -f ./data/docker-compose/postgres.yaml up -d
	@echo "Waiting 2 seconds..." && sleep 2
	@./gradlew :krm-debezium:rollback -PliquibaseCommandValue=$$DB_ROLLBACK_VERSION

krm-build-app: krm-db-migration
	@./gradlew clean :krm-debezium:build
	@docker build ./krm-debezium -t edd-krm:test --build-arg var_db_url=$$DB_URL_INTERNAL \
         --build-arg var_kafka_bootstrap_servers=$$KAFKA_BOOTSTRAP_SERVERS

krm-up: krm-build-app krm-db-migration
	@docker-compose -f ./data/docker-compose/postgres.yaml -f ./data/docker-compose/kafka.yaml -f \
     	./data/docker-compose/app-krm.yaml -f ./data/docker-compose/debezium.yaml up -d
	@docker ps
	@docker logs edd-krm -f

krm-down:
	@docker-compose -f ./data/docker-compose/postgres.yaml -f ./data/docker-compose/kafka.yaml -f \
       ./data/docker-compose/app-krm.yaml  -f ./data/docker-compose/debezium.yaml down

krm-send-email-request:
	curl -X POST "http://localhost:8080/send-email" -H "accept: application/json" -H "Content-Type: application/json" \
	 -d '{"recipients": ["me@shahinnazarov.com"], "eventId": "1234", "content": "sample content"}'

krm-add-connector-request:
	curl -X POST "http://localhost:8083/connectors/" -H "accept: application/json" -H "Content-Type: application/json"\
 		-d '{\
              "name": "postgres4-connector",  \
              "config": {\
                "connector.class": "io.debezium.connector.postgresql.PostgresConnector", \
                "database.hostname": "db", \
                "database.port": "5432", \
                "database.user": "edd", \
                "database.password": "secret", \
                "database.dbname" : "edd", \
                "database.server.name": "postgres", \
                "plugin.name": "pgoutput",\
                "topic.creation.enable": true,\
                "topic.creation.default.replication.factor": 1,\
                "topic.creation.default.partitions": 1\
              }\
            }'

## KEH SCOPE
keh-build-app:
	@./gradlew clean :kafka-error-handling:build
	@docker build ./kafka-error-handling -t edd-keh:test --build-arg var_db_url=$$DB_URL_INTERNAL \
		--build-arg var_kafka_bootstrap_servers=$$KAFKA_BOOTSTRAP_SERVERS

keh-up: keh-build-app
	@docker-compose -f ./data/docker-compose/postgres.yaml -f ./data/docker-compose/kafka.yaml -f \
	./data/docker-compose/app-keh.yaml up -d
	@docker ps
	@docker logs edd-keh -f

keh-down:
	@docker-compose -f ./data/docker-compose/postgres.yaml -f ./data/docker-compose/kafka.yaml -f \
	./data/docker-compose/app-keh.yaml up -d

kss-build-app:
	@./gradlew clean :krm-save-send:build
	@docker-compose -f ./data/docker-compose/postgres.yaml up -d
	@echo "Waiting 2 seconds..." && sleep 2
	@./gradlew :krm-save-send:update
	@docker build ./krm-save-send -t edd-krm-kss:test --build-arg var_db_url=$$DB_URL_INTERNAL \
		--build-arg var_kafka_bootstrap_servers=$$KAFKA_BOOTSTRAP_SERVERS

kss-up: kss-build-app
	@docker-compose -f ./data/docker-compose/postgres.yaml -f ./data/docker-compose/kafka-transactional.yaml -f \
	./data/docker-compose/app-krm-kss.yaml up -d
	@docker ps
	@docker logs edd-krm-kss -f

kss-down:
	@docker-compose -f ./data/docker-compose/postgres.yaml -f ./data/docker-compose/kafka-transactional.yaml -f \
	./data/docker-compose/app-krm-kss.yaml down

kss-db-rollback-migration:
	@docker-compose -f ./data/docker-compose/postgres.yaml up -d
	@echo "Waiting 2 seconds..." && sleep 2
	@./gradlew :krm-save-send:rollback -PliquibaseCommandValue=$$DB_ROLLBACK_VERSION

