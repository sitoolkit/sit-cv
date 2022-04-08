import request from 'sync-request';

class Config {
  private static INCETANCE: Config;
  private serverMode!: boolean;

  constructor() {
    if (this.endpoint.startsWith('http')) {
      var response = request('GET', this.endpoint + '/assets/designdoc-list.js');
      this.serverMode = response.statusCode === 404;
    } else {
      this.serverMode = false;
    }
  }

  public static get instance() {
    if (!this.INCETANCE) {
      this.INCETANCE = new Config();
    }
    return this.INCETANCE;
  }

  public get isServerMode() {
    return this.serverMode;
  }

  public get endpoint(): string {
    if (process && process.env.VUE_APP_ENDPOINT) {
      return process.env.VUE_APP_ENDPOINT;
    }
    return location.href.substring(0, location.href.indexOf('#')).replace('/index.html', '');
  }
}

export default Config.instance;
