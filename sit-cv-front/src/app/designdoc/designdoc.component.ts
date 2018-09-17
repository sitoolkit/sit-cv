import { Component, OnInit } from '@angular/core';
import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

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

}
