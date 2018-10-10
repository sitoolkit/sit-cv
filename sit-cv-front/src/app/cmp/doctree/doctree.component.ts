import { Component, OnChanges, Input, Output, EventEmitter, SimpleChanges } from '@angular/core';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';

class Node {
  children: Node[] = [];
  name: string;
  designDocId: string;
}

@Component({
  selector: 'app-doctree',
  templateUrl: './doctree.component.html',
  styleUrls: ['./doctree.component.css']
})
export class DoctreeComponent implements OnChanges {

  @Input() designDocIds: string[];
  @Output() selected = new EventEmitter<string>();

  nestedTreeControl = new NestedTreeControl<Node>((node: Node) => node.children);
  nestedDataSource = new MatTreeNestedDataSource();

  constructor() { }

  hasNestedChild = (_: number, nodeData: Node) => !nodeData.designDocId;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['designDocIds']) {
      this.nestedDataSource.data = this.createTree(this.designDocIds);
    }
  }

  onSelected(designDocId: string) {
    this.selected.emit(designDocId);
    return false;
  }

  toggleExpanded(node: Node) {
    if (this.nestedTreeControl.isExpanded(node)) {
      this.nestedTreeControl.collapse(node);
    } else {
      this.expandRecursively(node);
    }
  }

  expandRecursively(node: Node) {
    this.nestedTreeControl.expand(node);
    if (node.children.length === 1) {
      this.expandRecursively(node.children[0]);
    }
  }

  createTree(designDocIds: string[]): Node[] {
    const nodes: Node[] = [];
    designDocIds.forEach(designDocId => {
      this.merge(nodes, [this.createNodeByDocId(designDocId)]);
    });
    return nodes;
  }

  createNodeByDocId(designDocId: string): Node {
    const pos = designDocId.search(/[^.]*\(.*/);
    const inners = designDocId.substr(0, pos - 1).split('.');
    const leaf = designDocId.substr(pos);

    let rootNode: Node = null;
    let parentNode: Node = null;

    inners.forEach(packageOrClass => {
      const creatingNode = new Node();
      creatingNode.name = packageOrClass;
      if (parentNode != null) {
        parentNode.children.push(creatingNode);
      } else {
        rootNode = creatingNode;
      }
      parentNode = creatingNode;
    });

    const leafNode = new Node();
    leafNode.name = leaf;
    leafNode.designDocId = designDocId;
    parentNode.children.push(leafNode);

    return rootNode;
  }

  merge(into: Node[], merging: Node[]) {
    merging.forEach(node => {
      const matched = into.filter(n => n.name === node.name);
      if (matched.length === 0) {
        into.push(node);
      } else {
        this.merge(matched[0].children, node.children);
      }
    });
  }

}
