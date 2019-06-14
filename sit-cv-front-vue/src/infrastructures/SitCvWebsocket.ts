import * as Stomp from 'webstomp-client';
import SockJS from 'sockjs-client';
import { AsyncSubject } from 'rxjs';

class SitCvWebsocket {
  private static INSTANCE: SitCvWebsocket;

  private socket!: WebSocket;
  private stompClient!: Stomp.Client;
  private connectionSource: AsyncSubject<Stomp.Frame | undefined> = new AsyncSubject();

  private constructor() {
    this.connect();
  }

  public static get instance(): SitCvWebsocket {
    if (!this.INSTANCE) {
      this.INSTANCE = new SitCvWebsocket();
    }
    return this.INSTANCE;
  }

  public subscribe(callback: (client: Stomp.Client) => void) {
    this.connectionSource.subscribe(() => {
      callback(this.stompClient);
    });
  }

  private connect() {
    this.socket = new SockJS('http://' + location.hostname + ':8080/gs-guide-websocket');
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.connect(
      {},
      (frame?: Stomp.Frame) => {
        this.connectionSource.next(frame);
        this.connectionSource.complete();
      },
      (error: Stomp.Frame | CloseEvent) => {
        this.connectionSource.error(error);
      }
    );
  }
}

export default SitCvWebsocket.instance;
