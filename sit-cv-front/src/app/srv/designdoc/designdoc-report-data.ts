import { DesignDocDetail } from "./designdoc-detail";
import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class DesignDocLocalData {
  idList: { [designDocId: string]: string } = {};
  detailList: { [designDocId: string]: DesignDocDetail } = {};
}