import { Component, OnInit } from '@angular/core';
import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { DomSanitizer } from '@angular/platform-browser';
import * as $ from 'jquery';

@Component({
  selector: 'app-designdoc',
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
  diagramComments = {};
  selectedMethodSignatures = [];
  currentMethodSignature = "";
  constructor(private sanitizer: DomSanitizer) {
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
      let docDetail: any = JSON.parse(response.body);
      this.diagramComments = docDetail.comments;
      this.renderDiagrams(docDetail.diagrams);
    });
  }

  renderDesingDocList(designDocIds: string[]) {
    this.designDocIds = designDocIds;
  }

  renderDiagrams(diagrams: object) {
    let trustDiagrams = {};
    Object.keys(diagrams).forEach((key) => {
      trustDiagrams[key] = this.sanitizer.bypassSecurityTrustHtml(diagrams[key]);
    });
    this.currentDiagrams = trustDiagrams;
  }

  showDesignDocDetail(designDocId) {
    if (this.currentDesignDocId) {
      this.unsubscribe(this.currentDesignDocId);
      this.currentDiagrams = {};
      this.selectedMethodSignatures = [];
    }
    this.currentDesignDocId = designDocId;
    this.subscribe(this.currentDesignDocId);
    this.stompClient.send("/app/designdoc/detail", {}, this.currentDesignDocId);
    this.isDiagramLoading = true;
    return false;
  }



  methodNameClick(event) {
    let link: JQuery = $(event.target).closest('a');
    if (link.length > 0) {
      this.toggleComment(link);
    }
    return false;
  }

  toggleComment(link: JQuery) {
    let title: string = link.attr('xlink:title');
    let index: number = this.selectedMethodSignatures.indexOf(title);
    if (index < 0) {
      this.selectedMethodSignatures.push(title);
    } else {
      this.selectedMethodSignatures.splice(index, 1);
    }
  }

  methodNameMouseover(event) {
    let link: JQuery = $(event.target).closest('a');
    this.currentMethodSignature = link.attr('xlink:title');
    return false;
  }
}
