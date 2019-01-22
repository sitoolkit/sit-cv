import { CrudMatrix } from "./crud-matrix";

export interface DataModelService {
  getCrud(
    callback: (crudMatrix: CrudMatrix) => void
  ): void
}