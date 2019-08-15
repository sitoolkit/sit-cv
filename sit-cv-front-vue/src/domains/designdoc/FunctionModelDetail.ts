import FunctionModelApiDoc from "./FunctionModelApiDoc";

export default interface FunctionModelDetail {
  diagrams: { [diagramId: string]: string };
  apiDocs: {
    [methodSignature: string]: FunctionModelApiDoc,
  };
}

