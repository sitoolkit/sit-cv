import * as Stomp from 'webstomp-client';
import MenuItem from '@/domains/designdoc/MenuItem';
import DesignDocService from './DesignDocService';
import SitCvWebsocket from '@/infrastructures/SitCvWebsocket';

class DesignDocServerService implements DesignDocService {
  private static INSTANCE: DesignDocServerService;

  private constructor() {}

  public static get instance(): DesignDocServerService {
    if (!this.INSTANCE) {
      this.INSTANCE = new DesignDocServerService();
    }
    return this.INSTANCE;
  }

  public async fetchMenuItems(): Promise<MenuItem[]> {
    return new Promise((resolve) => {
      SitCvWebsocket.subscribe((client: Stomp.Client) => {
        client.subscribe('/topic/designdoc/list', (response: Stomp.Message) => {
          resolve(JSON.parse(response.body));
        });
        client.send('/app/designdoc/list');
      });
    });
  }
}

export default DesignDocServerService.instance;
