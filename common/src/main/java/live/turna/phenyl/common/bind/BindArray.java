package live.turna.phenyl.common.bind;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * <b>BindArray</b><br>
 * {@link ArrayList} wrapper for binding requests.
 *
 * @since 2021/12/5 3:36
 */
public class BindArray extends ArrayList<BindMap> {

    /**
     * Add a BindMap object to BindArray.
     * Would try to remove all existing objects that have same elements as the provided one in array before adding, in order to keep requests unique.
     *
     * @param map The BindMap to be added.
     * @return {@code true} (as specified by {@link java.util.Collection#add})
     */
    @Override
    public boolean add(BindMap map) {
        if (map == null) return false;
        this.remove(map.userName());
        this.remove(map.userID());
        this.remove(map.code());
        super.add(map);
        return true;
    }

    /**
     * Try to match the BindArray that contains all BindMaps matching the key.
     *
     * @param key The string key to proceed search in BindArray. Could be userName or verification code.
     * @return The array object or null for no corresponding result.
     */
    @Nullable
    public BindArray get(String key) {
        BindArray matches = new BindArray();
        for (BindMap entry : this) {
            if (entry.match(key)) {
                matches.add(entry);
            }
        }
        return matches.isEmpty() ? null : matches;
    }

    /**
     * Try to match the BindArray that contains all BindMaps that matches the key.
     *
     * @param key The key to proceed search in BindArray, which is userID.
     * @return The array object or null for no corresponding result.
     */
    @Nullable
    public BindArray get(Long key) {
        BindArray matches = new BindArray();
        for (BindMap entry : this) {
            if (entry.match(key)) {
                matches.add(entry);
            }
        }
        return matches.isEmpty() ? null : matches;
    }

    /**
     * Try to end all BindMaps that matches the key.
     *
     * @param key The string key to proceed search in BindArray. Could be userName or verification code.
     */
    public void remove(String key) {
        BindArray matches = this.get(key);
        if (matches == null) return;
        for (BindMap entry : matches) {
            super.remove(entry);
        }
    }

    /**
     * Try to end all BindMaps that matches the key.
     *
     * @param key The key to proceed search in BindArray, which is userID.
     */
    public void remove(Long key) {
        BindArray matches = this.get(key);
        if (matches == null) return;
        for (BindMap entry : matches) {
            super.remove(entry);
        }
    }
}