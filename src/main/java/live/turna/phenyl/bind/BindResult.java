package live.turna.phenyl.bind;

import org.jetbrains.annotations.Nullable;

public record BindResult(@Nullable String oldUserName, Boolean querySucceeded, @Nullable String userName) {

    /**
     * If a binding record was found in database, return the old Minecraft username. If not, return NULL.
     *
     * @return Old Minecraft username if available, NULL if not.
     */
    public String getOldUserName() {
        return oldUserName;
    }

    /**
     * If the database query succeeded or username no change, return true. Return false only when database operation failed.
     *
     * @return Whether database query succeeded.
     */
    public Boolean getQuerySucceeded() {
        return querySucceeded;
    }

    /**
     * Return the new username.
     *
     * @return New Minecraft username.
     */
    public String getUserName() {
        return userName;
    }
}
