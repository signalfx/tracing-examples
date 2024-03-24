import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
  java
  kotlin("jvm") version "1.9.20"
  id("com.bmuschko.docker-remote-api") version "9.4.0"
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("ch.qos.logback:logback-classic:1.5.3")
  testImplementation(project(":common"))
}

fun dockerTask(server: String, phpVersion: String) = task<DockerBuildImage>("dockerImage$server$phpVersion") {
  images.add("splunk-test-images:opentelemetry-php-$phpVersion-$server")
  dockerFile.set(File("images/Dockerfile-$server"))
  inputDir.set(projectDir.resolve("images"))
  buildArgs.set(mapOf("PHP_VERSION" to phpVersion))
}

val dockerTasks = listOf(
  dockerTask("apache", "8.0"),
  dockerTask("apache", "8.1"),
  dockerTask("apache", "8.2"),
  dockerTask("apache", "8.3"),
  dockerTask("fpm", "8.0"),
  dockerTask("fpm", "8.1"),
  dockerTask("fpm", "8.2"),
  dockerTask("fpm", "8.3")
)

task("dockerImages") {
  dependsOn(dockerTasks)
}

tasks.test {
  useJUnitPlatform()
}
