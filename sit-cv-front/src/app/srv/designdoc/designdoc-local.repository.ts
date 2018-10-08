import { DesignDocDetail } from "./designdoc-detail";
import { DesignDocIdList } from "./designdoc-id-list";
import { Injectable } from "@angular/core";

interface Window {
  designDocsData: any
}
declare var window: Window;

@Injectable()
export class DesignDocLocalRepository {

  private localData: any;
  private idList: { [designDocId: string]: string }
  private detailList: { [designDocId: string]: DesignDocDetail }

  constructor() {
    this.localData = window.designDocsData;

    if (this.isReady()) {
      this.idList = this.localData.idList;
      this.detailList = this.localData.detailList;
    }
  }

  isReady(): boolean {
    return this.localData != null;
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