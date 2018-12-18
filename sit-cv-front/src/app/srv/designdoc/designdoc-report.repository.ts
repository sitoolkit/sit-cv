import { DesignDocDetail } from "./designdoc-detail";
import { DesignDocIdList } from "./designdoc-id-list";
import { Injectable } from "@angular/core";
import { ReportDataLoader } from "../shared/report-data-loader";

@Injectable({ providedIn: 'root' })
export class DesignDocReportRepository {

  private detailPathMap: { [id: string]: string };

  constructor(
    private loader: ReportDataLoader
  ) {}

  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void {
    this.loader.loadScript("assets/designdoc-id-list.js", (pathMap) => {
      this.detailPathMap = pathMap;
      let idList = new DesignDocIdList();
      idList.ids = Object.keys(pathMap);
      callback(idList);
    })
  }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    this.loader.loadScript(this.detailPathMap[designDocId], (detail) => {
      callback(detail);
    })
  }

}