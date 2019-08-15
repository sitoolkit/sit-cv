class Config {

  private static INCETANCE : Config;

  public static get instance() {
    if (!this.INCETANCE) {
      this.INCETANCE = new Config();
    }    
    return this.INCETANCE;
  }

  public get isServerMode() {
    return this.endpoint.startsWith('http');
  }
  
  public get endpoint() : string {
    if (process && process.env.VUE_APP_ENDPOINT) {
      return  process.env.VUE_APP_ENDPOINT;
    }
    return `${location.protocol}//${location.host}`;
  }
}

export default Config.instance;