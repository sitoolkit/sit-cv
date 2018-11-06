class DesignDocJavadocTag {
  label: string;
  contents: Array<string>;
}

export class DesignDocJavadoc {
  qualifiedClassName: string;
  methodDeclaration: string;
  deprecated: DesignDocJavadocTag;
  description: string;
  tags: {
    [name: string]: DesignDocJavadocTag
  };
}