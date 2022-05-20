plugins {
    kotlin("jvm")
    java
}

group = "live.turna.phenyl"
version = "1.1.12"
val platform = "velocity"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    compileOnly("com.velocitypowered", "velocity-api", "3.0.1")
    annotationProcessor("com.velocitypowered", "velocity-api", "3.0.1")

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
    }
//    from(configurations.runtimeClasspath.get().files.filter {
//        it.name.equals("common-${archiveVersion.get()}.jar")
//    }.map { zipTree(it) })
    from(configurations.runtimeClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("Phenyl-${archiveVersion.get()}-$platform.jar")
    destinationDirectory.set(file(project.rootProject.buildDir.path + "/libs"))
}
