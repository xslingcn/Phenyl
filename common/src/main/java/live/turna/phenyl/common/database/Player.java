package live.turna.phenyl.common.database;

import org.jetbrains.annotations.Nullable;

public record Player(@Nullable Integer id, @Nullable String uuid, @Nullable Long qqid, @Nullable String mcname) {
}
