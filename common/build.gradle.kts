plugins {
    kotlin("jvm")
    java
}

group = "live.turna.phenyl"
version = "1.1.11"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("net.kyori", "adventure-api", "4.10.1")
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

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}