package io.sitoolkit.cv.core.domain.crud;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrudReader {

    public Optional<CrudMatrix> read(Path path) {
        if (!path.toFile().exists()) {
            log.info("CRUD matrix not exists: {}", path);
            return Optional.empty();
        }

        log.info("Read CRUD matrix: {}", path);

        try {
            String matrixJson = FileUtils.readFileToString(path.toFile(),
                    SitFileUtils.DEFAULT_CHARSET);
            CrudMatrix matrix = JsonUtils.str2obj(matrixJson, CrudMatrix.class);

            return Optional.of(matrix);
        } catch (Exception e) {
            log.warn("Read CRUD matrix failed: {}", path, e);
            return Optional.empty();
        }
    }

}
