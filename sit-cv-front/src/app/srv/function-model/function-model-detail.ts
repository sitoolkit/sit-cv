import { FunctionModelApiDoc } from "./function-model-apidoc";

export class FunctionModelDetail {
  diagrams: { [diagramId: string]: string };
  apiDocs: {
    [methodSignature: string]: FunctionModelApiDoc,
  };
}