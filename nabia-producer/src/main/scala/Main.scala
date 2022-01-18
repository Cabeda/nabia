import java.io.FileReader
import java.util.Properties
import java.util.Optional
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.common.errors.TopicExistsException
import java.util.Collections
import org.apache.kafka.clients.producer._

import src.main.scala.models.RecordJSON
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import scala.util.Try

@main
def main(configFileName: String, topicName: String): Unit = {
  val MAPPER = new ObjectMapper
  val props = buildProperties(configFileName)
  val producer = new KafkaProducer[String, JsonNode](props)

  val callback = new Callback {
    override def onCompletion(
        metadata: RecordMetadata,
        exception: Exception
    ): Unit = {
      Option(exception) match {
        case Some(err) => println(s"Failed to produce: $err")
        case None      => println(s"Produced record at $metadata")
      }
    }
  }

  while (true) {
    val countRecord: RecordJSON = new RecordJSON()
    val key: String = "alice"
    val value: JsonNode = MAPPER.valueToTree(countRecord)
    val record = new ProducerRecord[String, JsonNode](topicName, key, value)
    producer.send(record, callback)
  }
  producer.flush()
  producer.close()
  println("Wrote ten records to " + topicName)
}

def buildProperties(configFileName: String): Properties = {
  val properties: Properties = new Properties
  properties.load(new FileReader(configFileName))
  properties.put(
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer"
  )
  properties.put(
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.connect.json.JsonSerializer"
  )
  properties
}

def createTopic(topic: String, clusterConfig: Properties): Unit = {
  val newTopic = new NewTopic(
    topic,
    Optional.empty[Integer](),
    Optional.empty[java.lang.Short]()
  );
  val adminClient = AdminClient.create(clusterConfig)
  Try(adminClient.createTopics(Collections.singletonList(newTopic)).all.get)
    .recover { case e: Exception =>
      // Ignore if TopicExistsException, which may be valid if topic exists
      if (!e.getCause.isInstanceOf[TopicExistsException])
        throw new RuntimeException(e)
    }
  adminClient.close()
}
