plugins {
    kotlin("jvm")
    java
    id("net.minecrell.plugin-yml.bungee") version "0.5.0"
}

group = "live.turna.phenyl"
version = "1.1.12"
val platform = "bungee"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    implementation(project(":common"))
    implementation("net.kyori", "adventure-platform-bungeecord", "4.1.0")
    compileOnly("net.md-5", "bungeecord-api", "1.16-R0.4")
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.17.1")
    compileOnly("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", "2.13.1")
    compileOnly("com.github.mifmif", "generex", "1.0.2")
    compileOnly("com.zaxxer", "HikariCP", "5.0.1")
    compileOnly("net.mamoe", "mirai-core-api", "2.10.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.36.0.3")
    compileOnly("mysql", "mysql-connector-java", "8.0.28")
    compileOnly("org.postgresql", "postgresql", "42.3.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}

tasks.jar {
    manifest {
        attributes("Implementation-Title" to "Phenyl")
        attributes("Implementation-Version" to archiveVersion.get())
        attributes("Main-Class" to "live.turna.phenyl.bungee.loader.PhenylBungeeLoader")
    }
    from(configurations.runtimeClasspath.get().files.filter {
        it.name.equals("common-${archiveVersion.get()}.jar") ||
                (it.name.contains("adventure")
                        && !it.name.contains("adventure-api")
                        && !it.name.contains("adventure-nbt")
                        && !it.name.contains("bungeecord")) ||
                it.name.contains("examination-api")
    }.map { zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("Phenyl-${archiveVersion.get()}-$platform.jar")
    destinationDirectory.set(file(project.rootProject.buildDir.path + "/libs"))
}

bungee {
    name = "Phenyl"
    version = version
    description = "Easy-to-use Chat bridge"
    main = "live.turna.phenyl.bungee.loader.PhenylBungeeLoader"
    author = "xslingcn"
}
