package live.turna.phenyl.dependency;

import com.google.common.base.Suppliers;
import live.turna.phenyl.Phenyl;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>Log4jLoader</b><br>
 * Load {@link org.apache.logging.log4j} if not found.
 *
 * @since 2022/1/22 17:55
 */
public class Log4jLoader {
    private static final transient Logger Logger = Phenyl.getInstance().getLogger();
    private static final transient File libs = new File(Phenyl.getInstance().getDataFolder(), "libs");

    public static void onLoad() throws IOException {
        if (!libs.exists()) {
            if (!libs.mkdir()) Logger.severe("Failed creating library directory");
            else Logger.info("Successfully created library directory");
        }
        try {
            Class.forName("org.apache.logging.log4j.LogManager");
        } catch (ClassNotFoundException e) {
            Set<Dependency> dependencies = new HashSet<>();
            dependencies.add(Dependency.LOG4JCORE);
            dependencies.add(Dependency.LOG4JAPI);
            for (Dependency dependency : dependencies) {
                File jarFile = new File(libs, dependency.getFileName());
                File md5File = new File(libs, dependency.getFileName() + ".md5");
                if (jarFile.exists() && md5File.exists()) {
                    if (checkLog4j(jarFile, md5File)) {
                        Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) Phenyl.getInstance().getClass().getClassLoader()))
                                .get().addURL(jarFile.toURI().toURL());
                        Logger.info(dependency.getFileName() + " loaded");
                        continue;
                    }
                }
                for (DependencyRepository repo : DependencyRepository.values()) {
                    if (jarFile.exists() && !jarFile.delete())
                        throw new IOException("Failed deleting broken file: " + jarFile.getPath());
                    if (md5File.exists() && !md5File.delete())
                        throw new IOException("Failed deleting broken file:" + md5File.getPath());
                    if (!jarFile.createNewFile() || !md5File.createNewFile())
                        throw new IOException("Failed downloading dependency: " + dependency.getFileName());
                    Logger.info("Downloading " + repo.getUrl() + dependency.getMavenRepoPath() + "...");
                    if (!repo.download(dependency.getMavenRepoPath(), jarFile))
                        Logger.warning("Failed downloading dependency: " + jarFile.getPath());
                    if (!repo.download(dependency.getMavenRepoPath() + ".md5", md5File))
                        Logger.warning("Failed downloading dependency: " + md5File.getPath());
                    if (checkLog4j(jarFile, md5File)) break;
                }
                if (checkLog4j(jarFile, md5File)) {
                    Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) Phenyl.getInstance().getClass().getClassLoader()))
                            .get().addURL(jarFile.toURI().toURL());
                    Logger.info(dependency.getFileName() + " loaded");
                    continue;
                }
                throw new IOException(i18n("failLibDown", dependency.getFileName()));
            }
        }
    }

    private static boolean checkLog4j(File jarFile, File md5File) throws IOException {
        try {
            String jarDigest = toHex(MessageDigest.getInstance("md5").digest(Files.readAllBytes(jarFile.toPath())));
            String md5Digest = Files.readString(md5File.toPath(), StandardCharsets.UTF_8).replace("\n", "");
            return jarDigest.equals(md5Digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String toHex(byte[] bytes) {
        char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}