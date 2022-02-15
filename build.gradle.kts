group = "live.turna"
version = "1.1.8"

plugins {
    java
    kotlin("jvm") version "1.6.10"
    id("net.minecrell.plugin-yml.bungee") version "0.5.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("net.md-5", "bungeecord-api", "1.16-R0.4")
    // load below at runtime
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.17.1")
    compileOnly("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", "2.13.1")
    compileOnly("com.github.mifmif", "generex", "1.0.2")
    compileOnly("com.zaxxer", "HikariCP", "5.0.1")
    compileOnly("net.mamoe", "mirai-core-api", "2.10.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.36.0.3")
    compileOnly("mysql", "mysql-connector-java", "8.0.28")
    compileOnly("org.postgresql", "postgresql", "42.3.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}

bungee {
    name = "Phenyl"
    version = version
    description = "Chat bridge for TurnALive"
    main = "live.turna.phenyl.Phenyl"
    author = "TurnALive"
}
