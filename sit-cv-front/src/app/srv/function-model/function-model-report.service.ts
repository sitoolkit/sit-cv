import { Injectable } from '@angular/core';
import { FunctionModelDetail } from './function-model-detail';
import { FunctionModelService } from './function-model.service';
import { ReportDataLoader } from '../shared/report-data-loader';

@Injectable()
export class FunctionModelReportService implements FunctionModelService {

  constructor(private reportLoader: ReportDataLoader) { }

  getDetail(
    designDocId: string,
    callback: (detail: FunctionModelDetail) => void
  ): void {
  }

}
