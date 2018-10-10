import { DesignDocDetail } from "./designdoc-detail";
import { DesignDocIdList } from "./designdoc-id-list";
import { Injectable } from "@angular/core";
import { ReportDataLoader } from "../shared/report-data-loader";
import { DesignDocLocalData } from "./designdoc-report-data";

@Injectable({ providedIn: 'root' })
export class DesignDocReportRepository {

  constructor(
    private loader: ReportDataLoader,
    private data: DesignDocLocalData
  ) {}

  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void {
    this.loader.loadScript("assets/designdoc-id-list.js", () => {
      let idList = new DesignDocIdList();
      idList.ids = Object.keys(this.data.idList);
      callback(idList);
    })
  }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    this.loader.loadScript(this.data.idList[designDocId], () => {
      let detail = this.data.detailList[designDocId];
      callback(detail);
    })
  }

}