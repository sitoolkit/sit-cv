package io.sitoolkit.cv.core.domain.crud;

import java.nio.file.Path;

import io.sitoolkit.cv.core.infra.util.JsonUtils;

public class CrudWriter {

    public void write(CrudMatrix matrix, Path path) {
        JsonUtils.obj2file(matrix, path);
    }

}
