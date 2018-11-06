import { DesignDocJavadoc } from "./designdoc-javadoc";

export class DesignDocDetail {
  diagrams: { [diagramId: string]: string };
  comments: {
    [diagramId: string]: { [methodSignature: string]: string }
  };
  javadocs: {
    [methodSignature: string]: DesignDocJavadoc,
  };
}