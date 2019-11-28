import { statSync } from 'fs';

class Config {

  private static INCETANCE : Config;

  public static get instance() {
    if (!this.INCETANCE) {
      this.INCETANCE = new Config();
    }    
    return this.INCETANCE;
  }

  public get isServerMode() {
    try {
      statSync("assets");
      return true;
    } catch(err) {
      if(err.code === 'ENOENT') return false;
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