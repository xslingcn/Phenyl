package live.turna.phenyl.common.command;

import live.turna.phenyl.common.config.Config;

/**
 * <b>GroupCommand</b><br>
 * List all the commands that could be called in QQ groups.
 *
 * @since 2022/5/3 23:48
 */
public enum GroupCommand {
    BIND(Config.bind_command, 2),
    CONFIRM(Config.confirm_command, 2),
    ONLINE(Config.online_command, 1),
    STATUS(Config.status_command, 1);

    final String prompt;
    final Integer argCnt;

    /**
     * @param prompt The command name.
     * @param argCnt The number of its arguments.
     */
    GroupCommand(String prompt, Integer argCnt) {
        this.prompt = prompt;
        this.argCnt = argCnt;
    }
}
