class DesignDocApiDocContent {
  label: string;
  contents: Array<string>;
}

export class DesignDocApiDoc {
  qualifiedClassName: string;
  annotations: Array<string>;
  methodDeclaration: string;
  deprecated: DesignDocApiDocContent;
  description: string;
  contents: {
    [name: string]: DesignDocApiDocContent
  };
}