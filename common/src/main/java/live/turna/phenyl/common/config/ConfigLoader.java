package live.turna.phenyl.common.config;

public interface ConfigLoader {
    /**
     * Load configuration.
     */
    void load();

    /**
     * Configuration validator.
     *
     * @return Whether the configuration is valid.
     */
    boolean postLoad();
}
