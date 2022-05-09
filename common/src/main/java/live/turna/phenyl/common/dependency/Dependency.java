package live.turna.phenyl.common.dependency;

/**
 * <b>Dependency</b><br>
 * Dependencies used by Phenyl.
 *
 * @since 2022/1/19 20:54
 */
public enum Dependency {
    LOG4JCORE("org.apache.logging.log4j", "log4j-core", "2.17.1"),
    LOG4JAPI("org.apache.logging.log4j", "log4j-api", "2.17.1"),
    MIRAI("net.mamoe", "mirai-core-all", "2.10.3", "all"),
    GENEREX("com.github.mifmif", "generex", "1.0.2"),
    JACKSON("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", "2.13.1"),
    AUTOMATON("dk.brics.automaton", "automaton", "1.11-8"),

    HIKARI("com.zaxxer", "HikariCP", "5.0.1"),
    SQLITE("org.xerial", "sqlite-jdbc", "3.36.0.3"),
    MYSQL("mysql", "mysql-connector-java", "8.0.28"),
    POSTGRESQL("org.postgresql", "postgresql", "42.3.2"),

    ADVENTUREAPI("net.kyori", "adventure-api", "4.10.1"),
    ADVENTUREBUNGEE("net.kyori", "adventure-platform-bungeecord", "4.1.0"),
    ADVENTURESERIALIZERBUNGEE("net.kyori", "adventure-text-serializer-bungeecord", "4.1.0");

    private final String mavenRepoPath;
    private final String fileName;
    private final String version;

    Dependency(String groupID, String artifactID, String version, String flag) {
        this.mavenRepoPath = groupID.replace(".", "/")
                + "/" + artifactID
                + "/" + version + "/"
                + artifactID
                + "-" + version
                + (flag.isEmpty() ? "" : ("-" + flag)) + ".jar";
        this.fileName = artifactID + "-" + version + ".jar";
        this.version = version;
    }

    Dependency(String groupID, String artifactID, String version) {
        this(groupID, artifactID, version, "");
    }

    public String getFileName() {
        return fileName;
    }

    public String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    public String getVersion() {
        return this.version;
    }
}