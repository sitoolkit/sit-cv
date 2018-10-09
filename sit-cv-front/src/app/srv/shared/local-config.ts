import { Injectable } from "@angular/core";

interface Window {
  localConfig: LocalConfig
}
declare var window: Window;

@Injectable({ providedIn: 'root' })
export class LocalConfig {
  enabled: boolean = false;

  constructor() {
    let config = window.localConfig;
    this.enabled = config.enabled;
  }
}