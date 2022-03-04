package live.turna.phenyl.dependency;

import live.turna.phenyl.Phenyl;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>URLClassLoaderAccess</b><br>
 * Provides access to {@link URLClassLoader}#addURL using sun.misc.Unsafe.
 *
 * @author lucko (https://github.com/LuckPerms/LuckPerms) udner MIT license.
 * @author Vaishnav Anil (https://github.com/slimjar/slimjar)
 * @since 2022/3/2 20:49
 */
public class URLClassLoaderAccess {
    private static final Unsafe UNSAFE;

    static {
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (Throwable t) {
            unsafe = null;
        }
        UNSAFE = unsafe;
    }

    private static boolean isSupported() {
        return UNSAFE != null;
    }

    private final Collection<URL> unopenedURLs;
    private final Collection<URL> pathURLs;

    @SuppressWarnings("unchecked")
    URLClassLoaderAccess(URLClassLoader classLoader) {
        super();

        Collection<URL> unopenedURLs;
        Collection<URL> pathURLs;
        try {
            Object ucp = fetchField(URLClassLoader.class, classLoader, "ucp");
            unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
            pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
        } catch (Throwable e) {
            unopenedURLs = null;
            pathURLs = null;
        }

        this.unopenedURLs = unopenedURLs;
        this.pathURLs = pathURLs;
    }

    private static Object fetchField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        long offset = UNSAFE.objectFieldOffset(field);
        return UNSAFE.getObject(object, offset);
    }

    public void addURL(@NotNull URL url) {
        if (this.unopenedURLs == null || this.pathURLs == null) {
            throw new NullPointerException("unopenedURLs or pathURLs");
        }

        this.unopenedURLs.add(url);
        this.pathURLs.add(url);
    }

    public static URLClassLoaderAccess create(URLClassLoader classLoader) {
        if (URLClassLoaderAccess.isSupported()) {
            return new URLClassLoaderAccess(classLoader);
        } else {
            if (Phenyl.getInstance().getI18n() != null)
                throw new UnsupportedOperationException(i18n("failAddUrl"));
            else
                throw new UnsupportedOperationException("""
                        Phenyl is unable to inject into the plugin URLClassLoaderAccess.
                        You may be able to fix this problem by adding the following command-line argument directly after the 'java' command in your start script:\s
                        '--add-opens java.base/java.lang=ALL-UNNAMED'""");
        }
    }
}