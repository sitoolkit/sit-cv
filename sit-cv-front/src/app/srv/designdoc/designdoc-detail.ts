import { DesignDocApiDoc } from "./designdoc-apidoc";


export class DesignDocDetail {
  diagrams: { [diagramId: string]: string };
  comments: {
    [diagramId: string]: { [methodSignature: string]: string }
  };
  apiDocs: {
    [methodSignature: string]: DesignDocApiDoc,
  };
}