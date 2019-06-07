import { Injectable } from '@angular/core';
import { ReportDataLoader } from '../shared/report-data-loader';
import { DataModelService } from './data-model.service';
import { CrudMatrix } from './crud-matrix';

@Injectable()
export class DataModelReportService implements DataModelService {

  constructor(private reportLoader: ReportDataLoader) { }

  getCrud(
    callback: (crudMatrix: CrudMatrix) => void
  ): void {
    this.reportLoader.loadScript("datamodel/crud/crud.js", (crudMatrix) => {
      callback(crudMatrix);
    })
  }

}
