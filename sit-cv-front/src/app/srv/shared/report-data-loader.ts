import { Injectable } from "@angular/core";
import { DesignDocLocalData } from "../designdoc/designdoc-report-data";
import { Config } from "./config";

interface Window {
  reportData: any
}
declare var window: Window;

@Injectable({ providedIn: 'root' })
export class ReportDataLoader {

  private loadedScripts = new Array<string>();

  constructor(
    private config: Config,
    private designDoc: DesignDocLocalData
  ) {
    if (this.config.isReportMode()) {
      window.reportData = {
        designDoc: this.designDoc
      };
    }
  }

  loadScript(scriptPath, callback: () => void) {
    if (this.loadedScripts.indexOf(scriptPath) >= 0) {
      callback();
    } else {
      let script = document.createElement("script");
      script.onload = () => {
        this.loadedScripts.push(scriptPath);
        document.body.removeChild(script);
        callback();
      }
      script.src = scriptPath;
      document.body.appendChild(script);
    }
  }

}