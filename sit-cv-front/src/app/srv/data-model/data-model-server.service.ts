import { Injectable } from "@angular/core";
import { DataModelService } from "./data-model.service";
import { CrudMatrix } from "./crud-matrix";
import { Http, Response } from '@angular/http';
import { ServerRestPathBuilder } from "../shared/server-rest-path-builder";

@Injectable({providedIn: 'root'})
export class DataModelServerService implements DataModelService {

  constructor(
    private http: Http,
    private pathBuilder: ServerRestPathBuilder
  ) {
  }

  getCrud(
    callback: (crudMatrix: CrudMatrix) => void
  ): void {
    this.http.get(this.pathBuilder.build("/designdoc/data/crud"))
      .subscribe((res: Response) => {
        callback(res.json())
      });
  }

}
