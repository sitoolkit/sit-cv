import { Injectable } from "@angular/core";

interface Window {
  config: Config
}
declare var window: Window;

export enum Mode {
  Server = "server",
  Report = "report",
}

@Injectable({ providedIn: 'root' })
export class Config {

  mode: Mode;
  webSocketEndpoint: string;

  constructor() {
    let config = window.config;
    this.mode = config.mode;
    this.webSocketEndpoint = config.webSocketEndpoint;
  }

  isReportMode(): boolean {
    return this.mode == Mode.Report;
  }

  isServerMode(): boolean {
    return this.mode == Mode.Server;
  }

}