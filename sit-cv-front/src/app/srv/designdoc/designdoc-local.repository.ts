import { DesignDocDetail } from "./designdoc-detail";
import { DesignDocIdList } from "./designdoc-id-list";
import { Injectable } from "@angular/core";
import { LocalData } from "../shared/local-data";

@Injectable()
export class DesignDocLocalRepository {

  private idList: { [designDocId: string]: string }
  private detailList: { [designDocId: string]: DesignDocDetail }

  constructor(private localData: LocalData) {
    if (this.localData.isReady) {
      this.idList = this.localData.designDoc.idList;
      this.detailList = this.localData.designDoc.detailList;
    }
  }

  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void {
    let idList = new DesignDocIdList();
    idList.ids = Object.keys(this.idList);
    callback(idList);
  }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    if (this.detailList[designDocId] == null) {
      let script = document.createElement("script");
      script.onload = () => {
        let detail = this.detailList[designDocId];
        callback(detail);
      }
      script.src = this.idList[designDocId];
      document.body.appendChild(script);
    } else {
      let detail = this.detailList[designDocId];
      callback(detail);
    }
  }

}