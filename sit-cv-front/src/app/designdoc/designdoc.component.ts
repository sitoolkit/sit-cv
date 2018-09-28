import { Component, OnInit } from '@angular/core';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

class Node {
  children: Node[] = [];
  name: string;
  designDocId: string;
}

@Component({
  templateUrl: './designdoc.component.html',
  styleUrls: ['./designdoc.component.css']
})
export class DesignDocComponent {
  stompClient = null;
  designDocIds = [];
  currentDesignDocId = '';
  currentDiagrams = [];
  objectKeys = Object.keys;

  nestedTreeControl = new NestedTreeControl<Node>((node: Node) => node.children);
  nestedDataSource = new MatTreeNestedDataSource();

  constructor() {
    this.connect();
  }

  connect() {
    var socket = new SockJS(`http://${location.hostname}:8080/gs-guide-websocket`);
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, (frame) => {
      this.stompClient.subscribe('/topic/designdoc/list', (response) => {
        this.renderDesingDocList(JSON.parse(response.body).designDocIds);
      });

      this.stompClient.send("/app/designdoc/list");
    });
  }

  unsubscribe(designDocId) {
    this.stompClient.unsubscribe('/topic/designdoc/detail/' + designDocId);
  }

  subscribe(designDocId) {
    this.stompClient.subscribe('/topic/designdoc/detail/' + designDocId, (response) => {
      this.renderDiagrams(JSON.parse(response.body).diagrams);
    });
  }

  renderDesingDocList(designDocIds) {
    this.designDocIds = designDocIds;
    this.nestedDataSource.data = this.createTree(designDocIds);
  }

  renderDiagrams(diagrams) {
    this.currentDiagrams = diagrams;
  }

  showDesignDocDetail(designDocId) {
    if (this.currentDesignDocId) {
      this.unsubscribe(this.currentDesignDocId);
    }
    this.currentDesignDocId = designDocId;
    this.subscribe(this.currentDesignDocId);
    this.stompClient.send("/app/designdoc/detail", {}, this.currentDesignDocId);
    return false;
  }

  hasNestedChild = (_: number, nodeData: Node) => !nodeData.designDocId;

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
