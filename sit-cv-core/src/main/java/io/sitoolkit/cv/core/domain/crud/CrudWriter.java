package io.sitoolkit.cv.core.domain.crud;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrudWriter {

    public void write(CrudMatrix matrix, Path path) {
        String matrixJson = JsonUtils.obj2str(matrix);

        log.info("Write CRUD matrix: {}", path);

        try {
            FileUtils.writeStringToFile(path.toFile(), matrixJson, SitFileUtils.DEFAULT_CHARSET);
        } catch (IOException e) {
            log.warn("Write CRUD matrix failed: {}", path, e);
        }
    }

}
