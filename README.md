# Testing Kafka and ActiveMQ

## Prerequisites

In order to run the code you will need to have Docker installed on your machine. Your default docker machine should be configured to have plenty of disk space (10GB+). 

## Kafka

### Starting the Kafka Broker(s)

For running Kafka I have made use of the Confluent Kafka stack Docker images, you can find their source [here](https://github.com/confluentinc/docker-images). You can clone this repository and configure/build these images yourself, or otherwise Docker will pull the pre-built images for you from the Docker hub.

The Kafka stack can be started by running the following commands:

`docker run -d --name zookeeper -p 2181:2181 confluent/zookeeper`

```docker run -d --name kafka -p 9092:9092 --link zookeeper:zookeeper --env KAFKA_ADVERTISED_HOST_NAME=`docker-machine ip \`docker-machine active\`` confluent/kafka```

Kafka should now be running on your docker machine (to find the IP: docker-machine ip <machinename>). The default machine name is 'default', which runs on 192.168.99.100 for me.

### Exposing Kafka via REST

To expose Kafka via a REST interface the Confluent REST proxy can be started as well:

`docker run -d --name rest-proxy -p 8082:8082 --link zookeeper:zookeeper --link kafka:kafka confluent/rest-proxy`

The REST interface can be accessed on your docker machine IP via port 8082. Further specifications of the REST interface can be found [here](https://github.com/confluentinc/kafka-rest).

### Supporting large messages

To support large messages by the Broker, include the following parameters when starting the Kafka Docker container:

`--env KAFKA_MAX_PARTITION_FETCH_BYTES=<maxbytes> --env KAFKA_MESSAGE_MAX_BYTES=<maxbytes> --env KAFKA_REPLICA_FETCH_MAX_BYTES=<maxbytes>`

For the consumer the following parameter must be set:

`max.partition.fetch.bytes=<maxbytes>`

For the the producer the following parameter must be set:

`max.request.size=<maxbytes>`

## ActiveMQ




