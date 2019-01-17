import { Component, OnInit } from '@angular/core';
import { DataModelServerService } from 'src/app/srv/data-model/data-model-server.service';
import { CrudMatrix } from 'src/app/srv/data-model/crud-matrix';
import { CrudType } from 'src/app/srv/data-model/crud-type';

interface CrudTableRow {
  functionId: string;
  tableCrudMap: { [tableName: string]: CrudType[] };
}

@Component({
  selector: 'app-crud',
  templateUrl: './crud.component.html',
  styleUrls: ['./crud.component.css']
})
export class CrudComponent implements OnInit {

  dataSource: CrudTableRow[];
  columns: string[];
  tableNames: string[];
  crudTypes: CrudType[] = CrudType.values();

  constructor(private dataModelService: DataModelServerService) { }

  ngOnInit() {
    this.dataModelService.getCrud((crud) => this.showCrudMatrix(crud));
  }

  showCrudMatrix(crudMatrix: CrudMatrix) {
    this.tableNames = crudMatrix.tableDefs;
    this.columns = ['functionId'].concat(crudMatrix.tableDefs);
    this.dataSource = []
    Object.keys(crudMatrix.crudRowMap).forEach((functionId) => {
      let crudRow = crudMatrix.crudRowMap[functionId];
      this.dataSource.push({
        functionId: functionId,
        tableCrudMap: crudRow.cellMap,
      });
    })
  }

  findCrudType(types: CrudType[], target: CrudType): CrudType | null {
    if (types == null) {
      return null;
    }
    return types.includes(target) ? target : null;
  }

}
