import { Injectable } from "@angular/core";
import { DataModelService } from "./data-model.service";
import { CrudMatrix } from "./crud-matrix";
import { Http, Response } from '@angular/http';
import { Config } from "../shared/config";

@Injectable({providedIn: 'root'})
export class DataModelServerService implements DataModelService {

  constructor(
    private http: Http,
    private config: Config
  ) {
  }

  getCrud(
    callback: (crudMatrix: CrudMatrix) => void
  ): void {
    this.http.get(this.config.httpEndpoint + "/designdoc/data/crud")
      .subscribe((res: Response) => {
        callback(res.json())
      });
  }

}
