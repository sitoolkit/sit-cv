import { Injectable } from '@angular/core';
import { FunctionModelDetail } from './function-model-detail';
import { FunctionModelService } from './function-model.service';
import { ReportDataLoader } from '../shared/report-data-loader';
import { AsyncSubject } from 'rxjs';

type DetailPathMap = { [id: string]: string };
interface DetailMap {
  detailMap: { [id: string]: FunctionModelDetail };
}

@Injectable()
export class FunctionModelReportService implements FunctionModelService {

  private detailPathMapSubject: AsyncSubject<boolean> = new AsyncSubject<boolean>();
  private detailPathMap: DetailPathMap;

  constructor(private reportLoader: ReportDataLoader) { }

  getDetail(
    functionId: string,
    callback: (detail: FunctionModelDetail) => void
  ): void {
    if (this.detailPathMap == null) {
      this.getDetailPathMap();
    }
    this.detailPathMapSubject.subscribe(() => {
      this.reportLoader.loadScript(this.detailPathMap[functionId], (detailMap: DetailMap) => {
        callback(detailMap.detailMap[functionId]);
      })
    })
  }

  getDetailPathMap(): void {
    this.reportLoader.loadScript("functionmodel/detail-path-map.js", (detailPathMap: DetailPathMap) => {
      this.detailPathMap = detailPathMap;
      this.detailPathMapSubject.next(true);
      this.detailPathMapSubject.complete();
    })
  }

}
