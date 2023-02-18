/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.5/userguide/building_java_projects.html
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
  // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
  // Formatting
  id("com.diffplug.spotless") version "6.15.0"

  // Apply the application plugin to add support for building a CLI application in Java.
  application
}

repositories {
  // Use Maven Central for resolving dependencies.
  mavenCentral()
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  format("misc") {
    // define the files to apply `misc` to
    target("*.gradle", "*.md", ".gitignore")

    // define the steps to apply to those files
    trimTrailingWhitespace()
    indentWithTabs() // or spaces. Takes an integer argument if you don't like 4
    endWithNewline()
  }
  java {
    // don't need to set target, it is inferred from java

    // apply a specific flavor of google-java-format
    googleJavaFormat()
    // fix formatting of type annotations
    formatAnnotations()
  }
  kotlin {
    // by default the target is every '.kt' and '.kts` file in the java sourcesets
    ktfmt() // has its own section below
    //    ktlint() // has its own section below
    //        diktat()   // has its own section below
    //        prettier() // has its own section below
  }
  kotlinGradle {
    target("*.gradle.kts") // default target for kotlinGradle
    ktlint() // or ktfmt() or prettier()
    ktfmt()
  }
}

val ktor_version = "2.2.3"

dependencies {
  // Align versions of all Kotlin components
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  // Use the Kotlin JDK 8 standard library.
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("io.kubernetes:client-java:17.0.0") { because("Kubernetes interactions") }
  implementation("io.kubernetes:client-java-extended:17.0.0") {
    because("Java kubectl equivalent commands - easier to deal with")
  }

  implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5") { because("Parse cli arguments") }

  implementation("com.google.code.gson:gson:2.10.1") { because("Emit JSON snippet for unifi") }

  implementation("io.ktor:ktor-client-core:$ktor_version") {
    because("Honestly working with the Java URL HTTP stuff is just yucky. ")
  }
  implementation("io.ktor:ktor-client-cio:$ktor_version") { because("Its easy enough") }
  implementation("io.ktor:ktor-client-content-negotiation:$ktor_version") {
    because("Easy serialization w/ ktor")
  }
  implementation("io.ktor:ktor-serialization-gson:$ktor_version") { because("GSON KTOR serialize") }
  implementation("io.ktor:ktor-client-logging:$ktor_version") { because("Log stuff in client") }

  implementation("io.github.microutils:kotlin-logging-jvm:3.0.4") { because("Kotlin logs!") }
  implementation("org.slf4j:slf4j-simple:2.0.6") { because("Kotlin logs! But the actual logger") }

  implementation("com.hierynomus:sshj:0.34.0") {
    because("Try to pull and push data over ssh to unifi controller")
  }
}

testing {
  suites {
    // Configure the built-in test suite
    val test by
        getting(JvmTestSuite::class) {
          // Use Kotlin Test test framework
          useKotlinTest()
        }
  }
}

application {
  // Define the main class for the application.
  mainClass.set("com.bdawg.metalbgp.AppKt")
  applicationName = "metal-unifi-bgp-sync"
}
