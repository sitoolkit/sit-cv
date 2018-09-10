import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-uml',
  templateUrl: './uml.component.html',
  styleUrls: ['./uml.component.css']
})
export class UmlComponent implements OnInit {
  stompClient = null;

  constructor() {
    this.connect();
  }

  ngOnInit() {
    var currentDesignDocId = '';

    $(document).on('click', '#list a', (e) => {
      if (currentDesignDocId) {
        this.unsubscribe(currentDesignDocId);
      }
      currentDesignDocId = _.unescape($(e.target).html());
      this.subscribe(currentDesignDocId);
      this.stompClient.send("/app/designdoc/detail", {}, currentDesignDocId);
    });
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
    var ul = $('<ul>');
    $.each(designDocIds, (idx, designDocId) => {
      ul.append('<li><a href="#">' + _.escape(designDocId) + '</a></li>');
    });
    $('#list').html(ul);
  }

  renderDiagrams(diagrams) {
    var html = '';
    $.each(diagrams, (id, data) => {
      html += '<img src="' + data + '"/>';
    });
    $('#designdoc').html(html);
    $('#designdoc img').css('display', 'block');
  }

}
