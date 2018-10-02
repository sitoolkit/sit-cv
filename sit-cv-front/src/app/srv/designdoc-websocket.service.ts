import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { AsyncSubject } from 'rxjs';
import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';

@Injectable()
export class DesignDocWebsocketService implements DesignDocService {

  private serverUrl: string = `http://${location.hostname}:8080/gs-guide-websocket`;
  private socket: SockJS;
  private stompClient: Stomp.Client;
  private connectionSource: AsyncSubject<any> = new AsyncSubject();

  constructor() {
    this.connect();
  }

  connect(headers: any = {}) {
    this.socket = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.connect(headers, (frame: Stomp.Frame) => {
      this.connectionSource.next(frame);
      this.connectionSource.complete();
    }, (error: string) => {
      this.connectionSource.error(error);
    });
  }

  getDesignDocIdList(): AsyncSubject<any> {
    let subject: AsyncSubject<any> = this.sendAndSubscribe(
      '/app/designdoc/list',
      '/topic/designdoc/list'
    );
    return subject;
  }

  getDesignDocDetail(designDocId: string): AsyncSubject<any> {
    let subscribeUrl: string = '/topic/designdoc/detail/' + designDocId;
    let subject: AsyncSubject<any> = this.sendAndSubscribe(
      '/app/designdoc/detail',
      subscribeUrl,
      {},
      designDocId
    );
    return subject;
  }

  private sendAndSubscribe(
    sendUrl: string,
    subscribeUrl: string,
    sendHeaders?: Stomp.StompHeaders,
    sendBody?: string
  ): AsyncSubject<any> {
    let subject: AsyncSubject<any> = new AsyncSubject();
    this.connectionSource.subscribe(() => {
      let subscriber: any = this.stompClient.subscribe(subscribeUrl, (response: any) => {
        subject.next(JSON.parse(response.body));
        subject.complete();
        subscriber.unsubscribe();
      });
      this.stompClient.send(sendUrl, sendHeaders, sendBody);
    })
    return subject;
  }

}
