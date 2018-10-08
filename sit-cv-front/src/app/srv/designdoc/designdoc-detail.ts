export class DesignDocDetail {
  diagrams: { [diagramId: string]: string };
  comments: {
    [diagramId: string]: { [methodSignature: string]: string }
  }
}