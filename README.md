# Testing Kafka and ActiveMQ

## Prerequisites

In order to run the code you will need to have Docker installed on your machine. Your default docker machine should be configured to have plenty of disk space (10GB+). 

## Kafka

### Starting the Kafka broker with Docker

For running Kafka I have made use of the Confluent Kafka stack Docker images, you can find their source at [https://github.com/confluentinc/docker-images](https://github.com/confluentinc/docker-images). You can clone this repository and configure/build these images yourself, or otherwise Docker will pull the pre-built images for you from the Docker hub.

The Kafka stack can be started by running the following commands:

`docker run -d --name zookeeper -p 2181:2181 confluent/zookeeper`

```docker run -d --name kafka -p 9092:9092 --link zookeeper:zookeeper --env KAFKA_ADVERTISED_HOST_NAME=`docker-machine ip \`docker-machine active\`` confluent/kafka```

Kafka should now be running on your docker machine (to find the IP: docker-machine ip <machinename>). The default machine name is 'default', which runs on 192.168.99.100 on my machine.

### Exposing Kafka via REST

To expose Kafka via a REST interface the Confluent REST proxy can be started as well:

`docker run -d --name rest-proxy -p 8082:8082 --link zookeeper:zookeeper --link kafka:kafka confluent/rest-proxy`

The REST interface can be accessed on your docker machine IP via port 8082. Further specifications of the REST interface can be found at [https://github.com/confluentinc/kafka-rest](https://github.com/confluentinc/kafka-rest).

### Kafka NodeJS consumer and producer

The NodeJS consumer and producer for Kafka require the [https://www.npmjs.com/package/kafka-node](https://www.npmjs.com/package/kafka-node) npm package. You can install it by performing `npm install kafka-node` in the directory of the producer and consumer javascript files. Run the programs using `node consumer.js` and `node producer.js`.

### Supporting large messages

To support large messages by the Broker, include the following parameters when starting the Kafka Docker container:

`--env KAFKA_MAX_PARTITION_FETCH_BYTES=<maxbytes> --env KAFKA_MESSAGE_MAX_BYTES=<maxbytes> --env KAFKA_REPLICA_FETCH_MAX_BYTES=<maxbytes>`

For the consumer the following parameter must be set:

`max.partition.fetch.bytes=<maxbytes>`

For the the producer the following parameter must be set:

`max.request.size=<maxbytes>`

### Enabling message compression

By setting the parameter `compression.codec=1` on the producer all messages produced by that producer are automatically compressed by the producer, stored in compressed format on the broker, and decompressed by the consumer. For more details see [https://cwiki.apache.org/confluence/display/KAFKA/Compression](https://cwiki.apache.org/confluence/display/KAFKA/Compression).

## ActiveMQ

### Starting the ActiveMQ broker with Docker

For running ActiveMQ I have made use of Rmohr's ActiveMQ Docker image. You can find the source at [https://github.com/rmohr/docker-activemq](https://github.com/rmohr/docker-activemq). You can clone this repository and configure/build these image yourself, or otherwise Docker will pull the pre-built image for you from the Docker hub.

You can start the broker with the following command:

`docker run -d --name activemq -p 61616:61616 -p 8161:8161 -p 61613:61613 rmohr/activemq`

ActiveMQ should now be running on your docker machine (to find the IP: docker-machine ip <machinename>). The default machine name is 'default', which runs on 192.168.99.100 on my machine.

The admin web interface can be reached in my case at 192.168.99.100:8161 (user: admin / pass: admin).

### Exposing ActiveMQ via REST

ActiveMQ natively supports REST so this is running out of the box. For detailed specifications see [http://activemq.apache.org/rest.html](http://activemq.apache.org/rest.html). 

### ActiveMQ NodeJS consumer and producer

The NodeJS consumer and producer for ActiveMQ require the [https://www.npmjs.com/package/stompit](https://www.npmjs.com/package/stompit) npm package. You can install it by performing `npm install stompit` in the directory of the producer and consumer javascript files. Run the programs using `node consumer.js` and `node producer.js`.

### Enabling message compression

ActiveMQ can compress messages by setting the parameter `useCompression=true` on the connection. 

