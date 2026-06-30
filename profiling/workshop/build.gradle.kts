plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.splunk.profiling.workshop.ServiceMain"
    }
}

dependencies {
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("io.opentelemetry:opentelemetry-api:1.62.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.29.0")

}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.eclipse.jetty" || requested.group == "org.eclipse.jetty.websocket") {
            useVersion("9.4.57.v20241219")
        }
    }
}
