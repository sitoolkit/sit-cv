import * as Stomp from '@stomp/stompjs';
import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { DesignDocDetail } from './designdoc-detail';
import { SitCvWebsocket } from '../shared/sit-cv-websocket';
import { MenuItem } from '../menu/menu-item';

@Injectable()
export class DesignDocServerService implements DesignDocService {

  private detailSubscriber: Stomp.StompSubscription;

  constructor(private socket: SitCvWebsocket) {
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
    this.socket.subscribe((client: Stomp.Client) => {
      this.detailSubscriber = client.subscribe(subscribeUrl, (response: any) => {
        let detail = (<DesignDocDetail>JSON.parse(response.body));
        callback(detail);
      });
      client.send('/app/designdoc/detail', {}, designDocId);
    })
  }

  getMenuList(
    callback: (menuItems: MenuItem[]) => void
  ): void {
    this.socket.subscribe((client: Stomp.Client) => {
      client.subscribe('/topic/designdoc/list', (response: any) => {
        let menuItems = JSON.parse(response.body);
        callback(menuItems);
      });
      client.send('/app/designdoc/list');
    })
  }

}
