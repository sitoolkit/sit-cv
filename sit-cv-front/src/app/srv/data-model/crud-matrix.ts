enum CrudType {
  C, R, U, D
}
class CrudRow {
  actionPath: string;
  cellMap: { [tableName: string]: CrudType[] };
  sqlTextMap: { [tableName: string]: string[] };
}
export class CrudMatrix {
  crudRowMap: { [functionId: string]: CrudRow };
  tableDefs: string[];
}