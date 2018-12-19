import { Injectable } from "@angular/core";
import { Config } from "./config";

interface Report {
  path: string;
  content: any;
}

@Injectable({ providedIn: 'root' })
export class ReportDataLoader {

  private callbacks: {
    [id: string]: (data: any) => void
  } = {};

  constructor(
    private config: Config
  ) {
    if (this.config.isReportMode()) {
      this.setMessageListener()
    }
  }

  loadScript(scriptPath, callback: (data: any) => void) {
    this.callbacks[scriptPath] = callback;
    let script = document.createElement("script");
    script.onload = () => {
      document.body.removeChild(script);
    }
    script.src = scriptPath;
    document.body.appendChild(script);
  }

  setMessageListener() {
    addEventListener("message", (event) => {
      if (event.source != window) return;

      let report: Report = event.data;
      console.log("Receive postMessage ", report);

      let callback = this.callbacks[report.path];
      if (callback != null) {
        this.callbacks[report.path](report.content);
        delete this.callbacks[report.path];
      }
    });
  }

}