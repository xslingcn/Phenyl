group = "live.turna"
version = "1.0.0-SNAPSHOT"

plugins {
    java
    kotlin("jvm") version "1.6.0"
    id("net.minecrell.plugin-yml.bungee") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")

}

dependencies {
    compileOnly("net.md-5", "bungeecord-api", "1.16-R0.4")
    api("net.mamoe", "mirai-core-api", "2.8.2")
    runtimeOnly("net.mamoe", "mirai-core", "2.8.2")
    api("org.apache.logging.log4j", "log4j-core", "2.17.0")
    api("com.github.mifmif", "generex", "1.0.2")
    api("com.zaxxer", "HikariCP", "5.0.0")
    api("org.xerial", "sqlite-jdbc", "3.36.0.3")
    api("mysql", "mysql-connector-java", "8.0.27")
    api("org.postgresql", "postgresql", "42.3.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}

tasks.jar {
    manifest {
        attributes("Implementation-Title" to project.name)
        attributes("Implementation-Version" to project.version)
        attributes("Main-Class" to "live.turna.phenyl.Phenyl")
    }
    from(configurations.runtimeClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

bungee {
    name = "Phenyl"
    version = version
    description = "Chat bridge for TurnALive"
    main = "live.turna.phenyl.Phenyl"
    author = "TurnALive"
}
