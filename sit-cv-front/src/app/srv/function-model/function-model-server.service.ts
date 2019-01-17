import * as Stomp from '@stomp/stompjs';
import { Injectable } from '@angular/core';
import { FunctionModelDetail } from './function-model-detail';
import { SitCvWebsocket } from '../shared/sit-cv-websocket';
import { FunctionModelService } from './function-model.service';

@Injectable()
export class FunctionModelServerService implements FunctionModelService {

  private detailSubscriber: Stomp.StompSubscription;

  constructor(private socket: SitCvWebsocket) {
  }

  getDetail(
    functionId: string,
    callback: (detail: FunctionModelDetail) => void
  ): void {
    if (this.detailSubscriber != null) {
      this.detailSubscriber.unsubscribe();
      this.detailSubscriber = null;
    }
    let subscribeUrl: string = '/topic/designdoc/detail/' + functionId;
    this.socket.subscribe((client: Stomp.Client) => {
      this.detailSubscriber = client.subscribe(subscribeUrl, (response: any) => {
        let detail = (<FunctionModelDetail>JSON.parse(response.body));
        callback(detail);
      });
      client.send('/app/designdoc/detail', {}, functionId);
    })
  }

}
