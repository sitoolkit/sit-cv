import { Injectable } from "@angular/core";
import { Config } from "./config";

interface ReportData {
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

      console.log("get message ", event);
      let data: ReportData = event.data;
      let callback = this.callbacks[data.path];
      if (callback != null) {
        this.callbacks[event.data.path](data.content);
        delete this.callbacks[event.data.path];
      }
    });
  }

}