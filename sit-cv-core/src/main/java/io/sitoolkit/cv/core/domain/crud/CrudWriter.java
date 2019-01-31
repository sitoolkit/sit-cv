package io.sitoolkit.cv.core.domain.crud;

import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrudWriter {

    public void write(CrudMatrix matrix, Path path) {
        SitFileUtils.createDirectories(path.getParent());

        JsonUtils.obj2file(matrix, path);

        log.info("Wrote CRUD matrix: {}", path);
    }

}
