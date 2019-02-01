package io.sitoolkit.cv.core.domain.crud;

import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrudReader {

    public Optional<CrudMatrix> read(Path path) {
        if (!path.toFile().exists()) {
            log.info("CRUD matrix doesn't exist: {}", path);
            return Optional.empty();
        }

        log.info("Read CRUD matrix: {}", path);

        return Optional.of(JsonUtils.file2obj(path, CrudMatrix.class));
    }

}
