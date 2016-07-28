# mqtesting

## Prerequisites

In order to run the code you will need to have Docker installed on your machine. Your default docker machine should be configured to have plenty of disk space (10GB+). 

## Kafka

### Docker images

For running Kafka I have made use of the Confluent Kafka stack Docker images, you can find their source [here](https://github.com/confluentinc/docker-images). You can clone this repository and configure/build these images yourself, or otherwise Docker will pull the pre-built images for you from the Docker hub.

The Kafka stack can be started by running the following commands:

`docker run -d --name zookeeper -p 2181:2181 confluent/zookeeper`
`docker run -d --name kafka -p 9092:9092 --link zookeeper:zookeeper --env KAFKA_MAX_PARTITION_FETCH_BYTES=10000000 --env KAFKA_MESSAGE_MAX_BYTES=10000000 --env KAFKA_REPLICA_FETCH_MAX_BYTES=10000000 --env KAFKA_ADVERTISED_HOST_NAME=\`docker-machine ip \`docker-machine active\`\` confluent/kafka`
`docker run -d --name rest-proxy -p 8082:8082 --link zookeeper:zookeeper --link kafka:kafka confluent/rest-proxy`


