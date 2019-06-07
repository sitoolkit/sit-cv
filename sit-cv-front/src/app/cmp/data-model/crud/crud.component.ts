import { Component, OnInit, Inject } from '@angular/core';
import { DataModelServerService } from 'src/app/srv/data-model/data-model-server.service';
import { CrudMatrix } from 'src/app/srv/data-model/crud-matrix';
import { CrudType } from 'src/app/srv/data-model/crud-type';
import { EnumUtils } from 'src/app/srv/shared/enum-utils';
import { HidePackagePipe } from 'src/app/pipe/hide-package.pipe';
import { DataModelService } from 'src/app/srv/data-model/data-model.service';

interface CrudTableRow {
  functionId: string;
  package: string;
  classAndMethod: string;
  actionPath: string;
  tableCrudMap: { [tableName: string]: CrudType[] };
  repositoryFunctions: string[];
}

interface SplittedFunctionId {
  package: string;
  classAndMethod: string;
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
  crudTypes: CrudType[] = this.enumUtils.values(CrudType);

  constructor(
    @Inject('DataModelService') private dataModelService: DataModelService,
    private enumUtils: EnumUtils,
    private hidePackagePipe: HidePackagePipe,
  ) { }

  ngOnInit() {
    this.isLoading = true;
    this.dataModelService.getCrud((crud) => {
      this.isLoading = false;
      this.showCrudMatrix(crud)
    });
  }

  showCrudMatrix(crudMatrix: CrudMatrix) {
    this.tableNames = crudMatrix.tableDefs;
    this.columns = ['functionId'].concat(crudMatrix.tableDefs);
    this.dataSource = []
    Object.keys(crudMatrix.crudRowMap).forEach((functionId) => {
      let crudRow = crudMatrix.crudRowMap[functionId];
      let splittedId = this.splitFunctionId(this.hidePackagePipe.transform(functionId, 'PARAM_TYPE_ONLY'));
      this.dataSource.push({
        functionId: functionId,
        package: splittedId.package,
        classAndMethod: splittedId.classAndMethod,
        actionPath: crudRow.actionPath,
        tableCrudMap: crudRow.cellMap,
        repositoryFunctions: crudRow.repositoryFunctions,
      });
    })
  }

  splitFunctionId(functionId: string): SplittedFunctionId {
    let matches = functionId.match(/(.*)\.(.*\..*\(.*)/);
    return {
      package: matches[1],
      classAndMethod: matches[2],
    };
}

  isTypeIncludes(types: CrudType[], target: CrudType): boolean {
    if (types == null) {
      return null;
    }
    return types.includes(target);
  }

}
