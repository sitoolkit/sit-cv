import { DesignDocDetail } from "./designdoc-detail";

export class DesignDocLocalData {
  idList: { [designDocId: string]: string }
  detailList: { [designDocId: string]: DesignDocDetail }
}