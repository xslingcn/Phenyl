package live.turna.phenyl.common.database;

import org.jetbrains.annotations.Nullable;

/**
 * A player data object.
 *
 * @param id     The auto increment id.
 * @param uuid   The Minecraft UUID.
 * @param qqid   The QQ ID.
 * @param mcname The Minecraft username.
 */
public record Player(@Nullable Integer id, @Nullable String uuid, @Nullable Long qqid, @Nullable String mcname) {
}
