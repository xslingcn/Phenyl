package live.turna.phenyl.common.commands;

import live.turna.phenyl.common.config.Config;

public enum GroupCommand {
    BIND(Config.bind_command, 2),
    CONFIRM(Config.confirm_command, 2),
    ONLINE(Config.online_command, 1),
    STATUS(Config.status_command, 1);

    final String prompt;
    final Integer argCount;

    GroupCommand(String prompt, Integer argCount) {
        this.prompt = prompt;
        this.argCount = argCount;
    }
}
