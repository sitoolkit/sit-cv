import { Injectable } from '@angular/core';
import { FunctionModelDetail } from './function-model-detail';
import { FunctionModelService } from './function-model.service';
import { ReportDataLoader } from '../shared/report-data-loader';

interface DetailMap {
  detailMap: { [id: string]: FunctionModelDetail };
}

@Injectable()
export class FunctionModelReportService implements FunctionModelService {

  constructor(private reportLoader: ReportDataLoader) { }

  getDetail(
    functionId: string,
    callback: (detail: FunctionModelDetail) => void
  ): void {
    this.reportLoader.loadScript(this.functionId2scriptPath(functionId), (detailMap: DetailMap) => {
      callback(detailMap.detailMap[functionId]);
    })
  }

  functionId2scriptPath(functionId: string): string {
    let matches = functionId.match(/(.*)\..*\(.*/);
    return matches[1].replace(/\./g, "/") + ".js";
  }

}
