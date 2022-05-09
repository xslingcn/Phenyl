package live.turna.phenyl.common.dependency;

import com.google.common.base.Suppliers;
import live.turna.phenyl.common.plugin.AbstractPhenyl;

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

import static live.turna.phenyl.common.dependency.DependencyManager.toHex;
import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>Log4jLoader</b><br>
 * Load {@link org.apache.logging.log4j} if not found.
 *
 * @since 2022/1/22 17:55
 */
public class Log4jLoader {
    private final transient File libs;
    private final transient AbstractPhenyl phenyl;
    private final transient Logger Logger;


    public Log4jLoader(AbstractPhenyl plugin){
        phenyl=plugin;
        libs=new File(phenyl.getDir(),"libs");
        Logger= phenyl.getNativeLogger();
    }

    public void onLoad() throws IOException {
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
                        Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) phenyl.getClass().getClassLoader()))
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
                    Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) phenyl.getClass().getClassLoader()))
                            .get().addURL(jarFile.toURI().toURL());
                    Logger.info(dependency.getFileName() + " loaded");
                    continue;
                }
                throw new IOException(i18n("failLibDown", dependency.getFileName()));
            }
        }
    }

    private boolean checkLog4j(File jarFile, File md5File) throws IOException {
        try {
            String jarDigest = toHex(MessageDigest.getInstance("md5").digest(Files.readAllBytes(jarFile.toPath())));
            String md5Digest = Files.readString(md5File.toPath(), StandardCharsets.UTF_8).replace("\n", "");
            return jarDigest.equals(md5Digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}