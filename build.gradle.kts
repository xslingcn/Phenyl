group = "live.turna"
version = "1.1.2"

plugins {
    java
    kotlin("jvm") version "1.6.0"
    id("net.minecrell.plugin-yml.bungee") version "0.5.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("net.md-5", "bungeecord-api", "1.16-R0.4")
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.17.0")
    // load below at runtime
    compileOnly("com.github.mifmif", "generex", "1.0.2")
    compileOnly("com.zaxxer", "HikariCP", "5.0.0")
    compileOnly("net.mamoe", "mirai-core-api", "2.9.0")
    compileOnly("net.mamoe", "mirai-core", "2.9.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.36.0.3")
    compileOnly("mysql", "mysql-connector-java", "8.0.27")
    compileOnly("org.postgresql", "postgresql", "42.3.1")
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
    exclude("META-INF/*.RSA", "META-INF/*.DSA", "META-INF/*.SF")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

bungee {
    name = "Phenyl"
    version = version
    description = "Chat bridge for TurnALive"
    main = "live.turna.phenyl.Phenyl"
    author = "TurnALive"
}
