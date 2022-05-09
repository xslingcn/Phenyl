package live.turna.phenyl.common.config;

public interface ConfigLoader {
    void load();

    boolean postLoad();
}
