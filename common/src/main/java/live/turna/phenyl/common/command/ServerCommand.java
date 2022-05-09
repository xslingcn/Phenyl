package live.turna.phenyl.common.command;

public enum ServerCommand {
    WELCOME("", 1, ""),
    HELP("help", 1, "phenyl.use.bind"),
    RELOAD("reload", 1, "phenyl.admin.reload"),
    SLIDER("slider", 2, "phenyl.admin.login"),
    LOGIN("login", 1, "phenyl.admin.login"),
    LOGOUT("logout", 1, "phenyl.admin.logout"),
    MUTE("mute", 2, "phenyl.admin.mute"),
    BIND("bind", 2, "phenyl.use.bind"),
    VERIFY("verify", 2, "phenyl.use.verify"),
    SAY("say", 2, "phenyl.use.say"),
    NOMESSAGE("nomessage", 1, "phenyl.use.nomessage");

    final String prompt;
    final Integer argCnt;
    final String permission;

    ServerCommand(String prompt, Integer argCnt, String permission) {
        this.prompt = prompt;
        this.argCnt = argCnt;
        this.permission = permission;
    }
}
