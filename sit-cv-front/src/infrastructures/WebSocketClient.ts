import * as Stomp from 'webstomp-client';
import SockJS from 'sockjs-client';
import Config from './Config';
import { AsyncSubject } from 'rxjs';

class WebSocketClient {
  private static INSTANCE: WebSocketClient;
  private socket!: WebSocket;
  private stompClient!: Stomp.Client;
  private stompClientSubject: AsyncSubject<Stomp.Frame> = new AsyncSubject();
  private subscriptions = new Map<string, Stomp.Subscription>();
  private executedCallbacks: Array<(messageBody: string) => void> = [];

  private constructor() {}

  public static get instance() {
    if (!this.INSTANCE) {
      this.INSTANCE = new WebSocketClient();
      if (Config.isServerMode) {
        this.INSTANCE.connect();
      }
    }
    return this.INSTANCE;
  }

  private connect() {
    this.socket = new SockJS(`${Config.endpoint}/gs-guide-websocket`);
    this.stompClient = Stomp.over(this.socket);
    // disable stomp client log
    this.stompClient.debug = () => {};

    this.stompClient.connect({}, (frame?: Stomp.Frame) => {
      if (frame) {
        this.stompClientSubject.next(frame);
        this.stompClientSubject.complete();
      }
    });
  }

  public subscribe(
    destination: string,
    callback: (messageBody: string) => void,
    sendDestination?: string,
    sendBody?: string
  ) {
    this.stompClientSubject.subscribe(() => {
      const subscription = this.stompClient.subscribe(destination, (message) => {
        if (!this.executedCallbacks.includes(callback)) {
          callback(message.body);
          this.executedCallbacks.push(callback);
        }
      });

      this.subscriptions.set(destination, subscription);

      if (sendDestination) {
        this.send(sendDestination, sendBody);
      }
    });
  }

  public unsubscribe(destination: string) {
    this.stompClientSubject.subscribe(() => {
      const subscription = this.subscriptions.get(destination);
      if (subscription) {
        subscription.unsubscribe();
        this.subscriptions.delete(destination);
      }
    });
  }

  public send(destination: string, body?: string) {
    this.stompClientSubject.subscribe(() => {
      this.stompClient.send(destination, body);
    });
  }
}

export default WebSocketClient.instance;
