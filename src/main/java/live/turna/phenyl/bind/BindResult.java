package live.turna.phenyl.bind;

import live.turna.phenyl.database.Player;
import org.jetbrains.annotations.Nullable;

public record BindResult(Player registered, Boolean succeeded, @Nullable String userName) {

    public Player getRegistered() {
        return registered;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public String getUserName() {
        return userName;
    }
}
