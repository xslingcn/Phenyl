package live.turna.phenyl.common.bind;

import org.jetbrains.annotations.Nullable;

/**
 * oldUserName     If a binding record was found in database, return the old Minecraft username. If not, return NULL.
 * querySucceeded  If the database query succeeded or username no change, return true. Return false only when database operation failed.
 * userName        the new username.
 */
public record BindResult(@Nullable String oldUserName, Boolean querySucceeded, @Nullable String userName) {
}
