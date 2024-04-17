plugins {
  id("idea")
}

group = "com.splunk"

subprojects {
  version = rootProject.version

  apply(plugin = "splunk.spotless-conventions")
}
