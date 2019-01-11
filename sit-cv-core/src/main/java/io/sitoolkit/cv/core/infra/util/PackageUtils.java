package io.sitoolkit.cv.core.infra.util;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public class PackageUtils {

    public static String getVersion() {
        String version = PackageUtils.class.getPackage().getImplementationVersion();
        if (version != null) {
            return version;
        }

        File[] files = new File("./target").listFiles((FilenameFilter) new WildcardFileFilter("*.jar"));
        if (files.length == 0) {
            throw new RuntimeException("Packaged jar not found");
        }
        return JarUtils.getImplementationVersion(files[0].getAbsolutePath());
    }
}
