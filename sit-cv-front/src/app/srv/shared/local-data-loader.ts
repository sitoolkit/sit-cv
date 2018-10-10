import { Injectable } from "@angular/core";
import { DesignDocLocalData } from "../designdoc/designdoc-local-data";
import { Config } from "./config";

interface Window {
  localData: any
}
declare var window: Window;

@Injectable({ providedIn: 'root' })
export class LocalDataLoader {

  private loadedScripts = new Array<string>();

  constructor(
    private config: Config,
    private designDoc: DesignDocLocalData
  ) {
    if (this.config.isReportMode()) {
      window.localData = {
        designDoc: this.designDoc
      };
    }
  }

  loadScript(scriptPath, callback: () => void) {
    if (this.loadedScripts.includes(scriptPath)) {
      callback();
    } else {
      let script = document.createElement("script");
      script.onload = () => {
        document.body.removeChild(script);
        callback();
      }
      script.src = scriptPath;
      document.body.appendChild(script);
    }
  }

}