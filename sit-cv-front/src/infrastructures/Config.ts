import request from 'sync-request';

class Config {

  private static INCETANCE : Config;

  public static get instance() {
    if (!this.INCETANCE) {
      this.INCETANCE = new Config();
    }    
    return this.INCETANCE;
  }

  public get isServerMode() {
    if (this.endpoint.startsWith('http')) {
      var response = request("GET", this.endpoint + "/assets/designdoc-list.js");
      return response.statusCode !== 200;

    } else {
      return false;
    }
  }
  
  public get endpoint() : string {
    if (process && process.env.VUE_APP_ENDPOINT) {
      return  process.env.VUE_APP_ENDPOINT;
    }
    return `${location.protocol}//${location.host}`;
  }
}

export default Config.instance;