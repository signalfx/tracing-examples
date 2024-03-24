plugins {
  java
  kotlin("jvm") version "1.9.20"
  id("com.bmuschko.docker-remote-api") version "9.4.0"
}

repositories {
  mavenCentral()
}

dependencies {
  api("org.junit.jupiter:junit-jupiter:5.10.2")
  api("org.testcontainers:testcontainers:1.19.7")
  api("com.squareup.okhttp3:okhttp:4.12.0")
  api("io.opentelemetry.proto:opentelemetry-proto:1.1.0-alpha")
  api("com.fasterxml.jackson.core:jackson-databind:2.17.0")
  api("com.google.protobuf:protobuf-java-util:3.23.4")
}
