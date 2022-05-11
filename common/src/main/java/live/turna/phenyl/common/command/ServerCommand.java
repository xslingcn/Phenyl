package live.turna.phenyl.common.command;

/**
 * <b>ServerCommand</b><br>
 * List all Phenyl commands that could be called in Minecraft.
 *
 * @since 2022/5/6 17:30
 */
public enum ServerCommand {
    HELP("help", 1, "phenyl.use.bind"),
    RELOAD("reload", 1, "phenyl.admin.reload"),
    SLIDER("slider", 2, "phenyl.admin.login"),
    LOGIN("login", 1, "phenyl.admin.login"),
    LOGOUT("logout", 1, "phenyl.admin.logout"),
    MUTE("mute", 2, "phenyl.admin.mute"),
    BIND("bind", 2, "phenyl.use.bind"),
    VERIFY("verify", 2, "phenyl.use.verify"),
    SAY("say", 2, "phenyl.use.say"),
    NOMESSAGE("nomessage", 1, "phenyl.use.nomessage"),
    AT("at", 2, "phenyl.use.at");

    final String prompt;
    final Integer argCnt;
    final String permission;

    /**
     * @param prompt     The command name.
     * @param argCnt     The number of its arguments.
     * @param permission The permission node needed to perform the command.
     */
    ServerCommand(String prompt, Integer argCnt, String permission) {
        this.prompt = prompt;
        this.argCnt = argCnt;
        this.permission = permission;
    }
}
