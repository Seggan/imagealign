plugins {
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version "8.3.2"
    application
}

group = "io.github.seggan"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass = "io.github.seggan.imagealign.MainKt"
}

tasks.shadowJar {
    archiveClassifier = ""
}
