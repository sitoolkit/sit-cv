import { DesignDocApiDoc } from "./designdoc-apidoc";


export class DesignDocDetail {
  diagrams: { [diagramId: string]: string };
  apiDocs: {
    [methodSignature: string]: DesignDocApiDoc,
  };
}