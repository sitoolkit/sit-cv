import { Injectable } from '@angular/core';
import { DesignDocDetail } from '../designdoc/designdoc-detail';
import { DesignDocReportRepository } from '../designdoc/designdoc-report.repository';
import { FunctionModelService } from './function-model.service';

@Injectable()
export class FunctionModelReportService implements FunctionModelService {

  constructor(private repository: DesignDocReportRepository) { }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    this.repository.getDetail(designDocId, callback);
  }

}
