export default interface FunctionModelApiDoc {
  qualifiedClassName: string;
  annotations: Array<string>;
  methodDeclaration: string;
  contents: Array<string>;
}