import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.10"
  application
}

group = "us.jwf"

repositories {
  mavenCentral()
  maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
  testImplementation(kotlin("test-junit"))
  testImplementation("com.google.truth:truth:1.1")
  implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}

tasks.test {
  useJUnit()
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
  manifest {
    attributes["Main-Class"] = "us.jwf.aoc.cli.MainKt"
  }
  from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
  }
}

application {
  mainClassName = "us.jwf.aoc.cli.MainKt"
}
