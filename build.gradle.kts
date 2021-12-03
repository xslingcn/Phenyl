group = "live.turna"
version = "1.0-SNAPSHOT"

plugins {
    java
    kotlin("jvm") version "1.5.10"
    id("net.minecrell.plugin-yml.bungee") version "0.5.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
    api("net.mamoe", "mirai-core", "2.8.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}

bungee {
    name = "Phenyl"
    version = "1.0-SNAPSHOT"
    description = "Chat bridge for TurnALive"
    main = "live.turna.phenyl.Phenyl"
    author = "TurnALive"
}
