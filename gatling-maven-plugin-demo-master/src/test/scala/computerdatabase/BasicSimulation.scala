package computerdatabase

import com.github.mnogu.gatling.kafka.Predef.kafka
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.kafka.clients.producer.ProducerConfig

import scala.concurrent.duration._

class UserFeeder extends Iterator[Map[String, String]] {
  override def hasNext: Boolean = true
  var i: Integer = 0
  override def next(): Map[String, String] = {
    i += 1
    return Map("user" -> s"user_${i}")
  }
}

class BasicSimulation extends Simulation {

  val kafkaConf = kafka
    // Kafka topic name
    .topic("sse")
    // Kafka producer configs
    .properties(
    Map(
      ProducerConfig.ACKS_CONFIG -> "1",
      // list of Kafka broker hostname and port pairs
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",

      // in most cases, StringSerializer or ByteArraySerializer
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringSerializer",
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringSerializer"))

  val httpProtocol = http
    .baseUrl("http://localhost:8080")

  val myCheck = sse.checkMessage("receivedEvent")
    .check(regex(""".*"""))

  val scnSSE = scenario("ServerSentEvents")
    .feed(new UserFeeder())
    .exec(
      sse("SSE Connect").connect("/sse/${user}")
    )
    .exec(
      sse("Set Check").setCheck.await(5 seconds)(myCheck)
    )
    .pause(1 second)
    .exec(sse("Close").close())

  val scnKafka = scenario("Kafka")
    .feed(new UserFeeder())
    .exec(
      kafka("Send event").send[String]("${user}")
    )

  setUp(
    // scnSSE.inject(rampUsers(10000) during (60 seconds)).protocols(httpProtocol),
    scnKafka.inject(
      rampUsersPerSec(0) to 5000 during(10 seconds),
      constantUsersPerSec(5000) during(10 seconds),
      rampUsersPerSec(5000) to 0 during(10 seconds),
    ).protocols(kafkaConf)
  )


}
