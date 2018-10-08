import { Injectable } from "@angular/core";
import { DesignDocLocalData } from "../designdoc/designdoc-local-data";

interface Window {
  localData: LocalData
}
declare var window: Window;

@Injectable()
export class LocalData {
  isReady: boolean = false;
  designDoc: DesignDocLocalData;

  constructor() {
    let data = window.localData;
    if (data != null) {
      this.isReady = true;
      this.designDoc = data.designDoc;
    }
  }
}