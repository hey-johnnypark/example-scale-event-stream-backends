# How to run the SSE / Gatling demo

* Start Kafka in Docker

```
docker run -p 2181:2181 -p 9092:9092 -e ADVERTISED_HOST=127.0.0.1  -e NUM_PARTITIONS=10 johnnypark/kafka-zookeeper
```

* Start demo sse app

```
pwd
example-scale-event-stream-backends/demo-sse

mvn spring-boot:run
```

* Run Gatling Test

```
pwd
example-scale-event-stream-backends/gatling-maven-plugin-demo-master

mvn gatling:test -Dgatling.simulationClass=computerdatabase.BasicSimulation
```