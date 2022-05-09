group = "live.turna.phenyl"
version = "1.1.12"

plugins {
    java
    kotlin("jvm") version "1.6.20"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}

tasks.jar {
    from("README.md")
    archiveFileName.set("Phenyl")
}