import { Injectable } from "@angular/core";
import { DesignDocLocalData } from "../designdoc/designdoc-local-data";
import { LocalConfig } from "./local-config";

interface Window {
  localData: any
}
declare var window: Window;

@Injectable({ providedIn: 'root' })
export class LocalDataLoader {

  private loadedScripts = new Array<string>();

  constructor(
    private localConfig: LocalConfig,
    private designDoc: DesignDocLocalData
  ) {
    if (this.localConfig.enabled) {
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
      script.onload = callback;
      script.src = scriptPath;
      document.body.appendChild(script);
    }
  }

}