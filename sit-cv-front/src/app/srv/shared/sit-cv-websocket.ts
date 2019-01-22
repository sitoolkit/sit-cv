import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { AsyncSubject } from 'rxjs';
import { Injectable } from '@angular/core';
import { Config } from './config';

@Injectable({ providedIn: 'root' })
export class SitCvWebsocket {

  private socket: SockJS;
  private stompClient: Stomp.Client;
  private connectionSource: AsyncSubject<Stomp.Frame> = new AsyncSubject();

  constructor(private config: Config) {
    if (config.isServerMode()) {
      this.connect();
    }
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

  subscribe(callback: (client: Stomp.Client) => void) {
    this.connectionSource.subscribe(() => {
      callback(this.stompClient);
    });
  }
}
