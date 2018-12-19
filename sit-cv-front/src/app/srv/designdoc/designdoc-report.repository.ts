import { DesignDocDetail } from "./designdoc-detail";
import { DesignDocIdList } from "./designdoc-id-list";
import { Injectable } from "@angular/core";
import { ReportDataLoader } from "../shared/report-data-loader";
import { AsyncSubject } from "rxjs";

type DetailPathMap = { [id: string]: string };

@Injectable({ providedIn: 'root' })
export class DesignDocReportRepository {

  private detailPathMapSubject: AsyncSubject<boolean> = new AsyncSubject<boolean>();
  private detailPathMap: DetailPathMap;

  constructor(
    private loader: ReportDataLoader
  ) { }

  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void {
    this.loader.loadScript("assets/designdoc-detail-path-map.js", (detailPathMap: DetailPathMap) => {
      this.detailPathMap = detailPathMap;
      let idList = new DesignDocIdList();
      idList.ids = Object.keys(detailPathMap);
      callback(idList);

      this.detailPathMapSubject.next(true);
      this.detailPathMapSubject.complete();
    })
  }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    this.detailPathMapSubject.subscribe(() => {
      this.loader.loadScript(this.detailPathMap[designDocId], (detailMap: { [id: string]: DesignDocDetail }) => {
        callback(detailMap[designDocId]);
      })
    })
  }

}