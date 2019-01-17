import { Component, OnInit } from '@angular/core';
import { DataModelServerService } from 'src/app/srv/data-model/data-model-server.service';
import { CrudMatrix } from 'src/app/srv/data-model/crud-matrix';

@Component({
  selector: 'app-crud',
  templateUrl: './crud.component.html',
  styleUrls: ['./crud.component.css']
})
export class CrudComponent implements OnInit {

  crudMatrix: string;

  constructor(private dataModelService: DataModelServerService) { }

  ngOnInit() {
    this.dataModelService.getCrud((crudMatrix) => {
      this.crudMatrix = JSON.stringify(crudMatrix);
    })
  }

}
