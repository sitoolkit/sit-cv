package org.sitoolkit.cv.core.infra;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;

public class SitRepository {


    public static Path getRepositoryPath() {

        if (SystemUtils.IS_OS_WINDOWS) {
            return Paths.get("C:\\ProgramData\\sitoolkit\\repository");
        } else {
            return Paths.get(System.getProperty("user.home"), ".sitoolkit/repository");
        }
    }

}
