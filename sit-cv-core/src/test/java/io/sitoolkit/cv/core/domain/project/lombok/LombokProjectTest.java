package io.sitoolkit.cv.core.domain.project.lombok;

import java.nio.file.Paths;

import org.junit.Test;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.maven.MavenProjectReader;

public class LombokProjectTest {

    @Test
    public void test() {
        Project p = (new MavenProjectReader()).read(Paths.get("../sample")).get();
        LombokProject lp = new LombokProject(p);
        lp.refresh();
        System.out.println(lp.getSrcDirs());
        System.out.println(lp.getWatchDirs());
    }

}
