plugins {
    kotlin("jvm") version "2.1.10"
}

group = "io.github.seggan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}
