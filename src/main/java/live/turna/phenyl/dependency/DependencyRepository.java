package live.turna.phenyl.dependency;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * <b>DependencyRepository</b><br>
 * Available maven repositories.
 *
 * @since 2022/1/20 2:27
 */
public enum DependencyRepository {

    Aliyun("https://maven.aliyun.com/repository/central/"),
    MAVEN_CENTRAL("https://repo1.maven.org/maven2/");

    final String url;

    DependencyRepository(String url) {
        this.url = url;
    }

    /**
     * Downloads the specified URL to the specified file location. Maximum size
     * allowed is <code>Long.MAX_VALUE</code> bytes.
     *
     * @param url  Location to read
     * @param file Location to write
     * @throws IOException Reading failed
     */
    public boolean download(URL url, File file) throws IOException {
        InputStream is = url.openStream();
        ReadableByteChannel rbc = Channels.newChannel(is);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            return fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE) != 0;
        } finally {
            if (fos != null) fos.close();
            is.close();
        }
    }

    public boolean download(String mavenPath, File file) throws IOException {
        return this.download(new URL(this.url + mavenPath), file);
    }
}