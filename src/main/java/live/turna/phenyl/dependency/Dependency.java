package live.turna.phenyl.dependency;

/**
 * <b>Dependency</b><br>
 * Dependencies used by Phenyl.
 *
 * @since 2022/1/19 20:54
 */
public enum Dependency {
    MIRAI("net.mamoe", "mirai-core-all", "2.9.0", "all"),
    GENEREX("com.github.mifmif", "generex", "1.0.2"),
    JACKSON("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", "2.13.1"),
    AUTOMATON("dk.brics.automaton", "automaton", "1.11-8"),
    HIKARI("com.zaxxer", "HikariCP", "5.0.0"),
    SQLITE("org.xerial", "sqlite-jdbc", "3.36.0.3"),
    MYSQL("mysql", "mysql-connector-java", "8.0.27"),
    POSTGRESQL("org.postgresql", "postgresql", "42.3.1");

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
        this.fileName = artifactID + ".jar";
        this.version = version;
    }

    Dependency(String groupID, String artifactID, String version) {
        this(groupID, artifactID, version, "");
    }

    String getFileName() {
        return fileName;
    }

    String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    String getVersion() {
        return this.version;
    }
}