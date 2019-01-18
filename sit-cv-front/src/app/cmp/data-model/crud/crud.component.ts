import { Component, OnInit } from '@angular/core';
import { DataModelServerService } from 'src/app/srv/data-model/data-model-server.service';
import { CrudMatrix } from 'src/app/srv/data-model/crud-matrix';
import { CrudType } from 'src/app/srv/data-model/crud-type';

interface CrudTableRow {
  functionId: string;
  actionPath: string;
  tableCrudMap: { [tableName: string]: CrudType[] };
  repositoryFunctions: string[];
}

@Component({
  selector: 'app-crud',
  templateUrl: './crud.component.html',
  styleUrls: ['./crud.component.css']
})
export class CrudComponent implements OnInit {

  isLoading: boolean = false;
  dataSource: CrudTableRow[];
  columns: string[];
  tableNames: string[];
  crudTypes: CrudType[] = CrudType.values();

  constructor(private dataModelService: DataModelServerService) { }

  ngOnInit() {
    this.isLoading = true;
    this.dataModelService.getCrud((crud) => {
      this.isLoading = false;
      this.showCrudMatrix(crud)
    });
  }

  showCrudMatrix(crudMatrix: CrudMatrix) {
    this.tableNames = crudMatrix.tableDefs;
    this.columns = ['functionId', 'actionPath'].concat(crudMatrix.tableDefs).concat(['repositoryFunctions']);
    this.dataSource = []
    Object.keys(crudMatrix.crudRowMap).forEach((functionId) => {
      let crudRow = crudMatrix.crudRowMap[functionId];
      this.dataSource.push({
        functionId: functionId,
        actionPath: crudRow.actionPath,
        tableCrudMap: crudRow.cellMap,
        repositoryFunctions: crudRow.repositoryFunctions,
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
