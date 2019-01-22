import * as Stomp from '@stomp/stompjs';
import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { SitCvWebsocket } from '../shared/sit-cv-websocket';
import { MenuItem } from '../menu/menu-item';

@Injectable()
export class DesignDocServerService implements DesignDocService {

  constructor(private socket: SitCvWebsocket) {
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
