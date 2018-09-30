import { Component, OnInit } from '@angular/core';
import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { DomSanitizer } from '@angular/platform-browser';
import * as $ from 'jquery';

@Component({
  templateUrl: './designdoc.component.html',
  styleUrls: ['./designdoc.component.css']
})
export class DesignDocComponent {
  stompClient = null;
  designDocIds = [];
  currentDesignDocId = '';
  currentDiagrams = {};
  objectKeys = Object.keys;
  isDiagramLoading = false;

  constructor(private sanitizer: DomSanitizer) {
    $(document).on('click', 'svg a', () => false);
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

  unsubscribe(designDocId: string) {
    this.stompClient.unsubscribe('/topic/designdoc/detail/' + designDocId);
  }

  subscribe(designDocId: string) {
    this.stompClient.subscribe('/topic/designdoc/detail/' + designDocId, (response) => {
      this.isDiagramLoading = false;
      this.renderDiagrams(JSON.parse(response.body).diagrams);
    });
  }

  renderDesingDocList(designDocIds: string[]) {
    this.designDocIds = designDocIds;
  }

  renderDiagrams(diagrams: object) {
    let trustDiagrams = {};
    Object.keys(diagrams).forEach((key) => {
      trustDiagrams[key] = this.sanitizer.bypassSecurityTrustHtml(diagrams[key].replace(/&amp;#13;&amp;#10;/g, "\n"));
    });
    this.currentDiagrams = trustDiagrams;
  }

  showDesignDocDetail(designDocId) {
    if (this.currentDesignDocId) {
      this.unsubscribe(this.currentDesignDocId);
    }
    this.currentDiagrams = {};
    this.currentDesignDocId = designDocId;
    this.subscribe(this.currentDesignDocId);
    this.stompClient.send("/app/designdoc/detail", {}, this.currentDesignDocId);
    this.isDiagramLoading = true;
    return false;
  }

}
