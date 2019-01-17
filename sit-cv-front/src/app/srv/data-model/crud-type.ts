export enum CrudType {
  C, R, U, D
}

export namespace CrudType {
  export function values(): CrudType[] {
    return Object.keys(CrudType).map(k => CrudType[k])
      .filter(v => typeof v === "string");
  }
}