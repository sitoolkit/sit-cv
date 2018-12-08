import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { AsyncSubject } from 'rxjs';
import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { DesignDocIdList } from './designdoc-id-list';
import { DesignDocDetail } from './designdoc-detail';
import { Config } from '../shared/config';

@Injectable()
export class DesignDocServerService implements DesignDocService {

  private socket: SockJS;
  private stompClient: Stomp.Client;
  private connectionSource: AsyncSubject<Stomp.Frame> = new AsyncSubject();
  private detailSubscriber: Stomp.StompSubscription;

  constructor(private config: Config) {
    this.connect();
  }

  connect() {
    let headers = {};
    this.socket = new SockJS(this.config.webSocketEndpoint);
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.connect(headers, (frame: Stomp.Frame) => {
      this.connectionSource.next(frame);
      this.connectionSource.complete();
    }, (error: string) => {
      this.connectionSource.error(error);
    });
  }

  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void {
    this.connectionSource.subscribe(() => {
      this.stompClient.subscribe('/topic/designdoc/list', (response: any) => {
        let idList = new DesignDocIdList();
        idList.ids = JSON.parse(response.body).designDocIds;
        callback(idList);
      });
      this.stompClient.send('/app/designdoc/list');
    })
  }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    if (this.detailSubscriber != null) {
      this.detailSubscriber.unsubscribe();
      this.detailSubscriber = null;
    }
    let subscribeUrl: string = '/topic/designdoc/detail/' + designDocId;
    this.connectionSource.subscribe(() => {
      this.detailSubscriber = this.stompClient.subscribe(subscribeUrl, (response: any) => {
        let detail = (<DesignDocDetail>JSON.parse(response.body));
        callback(detail);
      });
      this.stompClient.send('/app/designdoc/detail', {}, designDocId);
    })
  }

}
