package live.turna.phenyl.bind;

import org.jetbrains.annotations.Nullable;

/**
 * <b>BindMap</b><br>
 * Type stores each verification request with username, user's QQ id and verification code.
 *
 * @since 2021/12/5 15:36
 */
public record BindMap(String userName, Long userID, String code) {

    /**
     * To check if the key matches the map.
     *
     * @param key The string key to proceed matching. Could be userName or verification code.
     * @return The BindMap object if matches and null for not so.
     */
    @Nullable
    public BindMap match(String key) {
        if (userName.equalsIgnoreCase(key) || code.equals(key)) return this;
        return null;
    }

    /**
     * To check if the key matches the map.
     *
     * @param key The key to proceed matching, which is userID.
     * @return The BindMap object if matches and null for not so.
     */
    @Nullable
    public BindMap match(Long key) {
        if (userID.equals(key)) return this;
        return null;
    }

    /**
     * Get userName string.
     *
     * @return The userName.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Get code string.
     *
     * @return The code.
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Get userID.
     *
     * @return The userID.
     */
    public Long getUserID() {
        return this.userID;
    }

}