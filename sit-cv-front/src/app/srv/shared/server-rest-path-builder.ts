import { Injectable } from "@angular/core";
import { Config } from "./config";
import { Location } from "@angular/common";

@Injectable({ providedIn: 'root' })
export class ServerRestPathBuilder {

  constructor(private config: Config) {}

  build(path: string): string {
    return Location.joinWithSlash(this.config.httpEndpoint, path);
  }
}