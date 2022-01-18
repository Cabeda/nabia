val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nabia-producer",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    resolvers += "confluent" at "https://packages.confluent.io/maven/", // https://mvnrepository.com/artifact/io.confluent/kafka-json-schema-serializer
    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "com.typesafe" % "config" % "1.3.2",
      "org.apache.kafka" % "kafka-clients" % "3.0.0",
      "org.apache.kafka" % "connect-json" % "3.0.0",
      "org.apache.kafka" % "connect-runtime" % "3.0.0",
      // "org.apache.kafka" % "kafka-streams" % "3.0.0",
      // "org.apache.kafka" %% "kafka-streams-scala" % "3.0.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.5",
      "org.apache.kafka" % "connect-runtime" % "2.1.0",
      "io.confluent" % "kafka-json-serializer" % "6.0.1",
      "javax.ws.rs" % "javax.ws.rs-api" % "2.1.1" artifacts (Artifact(
        "javax.ws.rs-api",
        "jar",
        "jar"
      )) // this is a workaround for https://github.com/jax-rs/api/issues/571
    )
  )
