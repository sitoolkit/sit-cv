package io.sitoolkit.cv.core.domain.crud;

import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.infra.util.JsonUtils;

public class CrudReader {

    public Optional<CrudMatrix> read(Path path) {
        return JsonUtils.file2obj(path, CrudMatrix.class);
    }

}
