package live.turna.phenyl.bind;

import org.jetbrains.annotations.Nullable;

public record BindResult(@Nullable String oldUserName, Boolean querySucceeded, @Nullable String userName) {

    public String getOldUserName() {
        return oldUserName;
    }

    public Boolean getQuerySucceeded() {
        return querySucceeded;
    }

    public String getUserName() {
        return userName;
    }
}
