package io.sitoolkit.cv.core.domain.report.crud;

import io.sitoolkit.cv.core.domain.crud.CrudMatrix;
import io.sitoolkit.cv.core.domain.report.Report;

public class CrudReportProcessor {

    public Report<?> process(CrudMatrix crudMatrix) {
        return Report.builder().path("datamodel/crud/crud.js").content(crudMatrix).build();
    }
}
