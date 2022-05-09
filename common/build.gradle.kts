plugins {
    kotlin("jvm")
    java
}

group = "live.turna.phenyl"
version = "1.1.12"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    implementation("net.kyori", "adventure-api", "4.10.1")
    implementation("net.kyori", "adventure-platform-api", "4.1.0")
    implementation("net.kyori", "adventure-platform-facet", "4.1.0")
    implementation("net.kyori", "adventure-text-serializer-gson", "4.10.0")
    implementation("net.kyori", "adventure-text-serializer-legacy", "4.10.0")
    compileOnly("com.google.guava", "guava", "31.1-jre")
    compileOnly("com.google.code.gson", "gson", "2.9.0")
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.17.1")
    compileOnly("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", "2.13.1")
    compileOnly("com.github.mifmif", "generex", "1.0.2")
    compileOnly("com.zaxxer", "HikariCP", "5.0.1")
    compileOnly("net.mamoe", "mirai-core-api", "2.10.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.36.0.3")
    compileOnly("mysql", "mysql-connector-java", "8.0.28")
    compileOnly("org.postgresql", "postgresql", "42.3.2")
}

tasks.jar {
    manifest {
        attributes("Implementation-Title" to project.name)
        attributes("Implementation-Version" to project.version)
    }
    from(configurations.runtimeClasspath.get().files.filter {
        (it.name.contains("adventure")
                && !it.name.contains("adventure-api")
                && !it.name.contains("adventure-nbt")) ||
                it.name.contains("examination-api")
    }.map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}