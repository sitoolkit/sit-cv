import Config from './Config';

class ScriptLoader {
  private static INSTANCE: ScriptLoader;

  private callbacks = new Map<string, (data:any) => void>();

  public static get incetance() {
    if (!this.INSTANCE) {
      this.INSTANCE = new ScriptLoader();

      if (!Config.isServerMode) {
        this.INSTANCE.setMessageListener();
      }
    }
    return this.INSTANCE;
  }

  public load(scriptPath:string, callback:(loadedData:any) => void) {

    const script = document.createElement('script');
    script.onload = () => {
      document.body.removeChild(script);
    };
    script.src = scriptPath;
    document.body.appendChild(script);

    this.callbacks.set(scriptPath, callback);
  }

  setMessageListener() {
    addEventListener("message", (event) => {
      if (event.source != window) {
        return;
      } 

      const loadedData = event.data;
      console.log("Receive postMessage ", loadedData);

      const callback = this.callbacks.get(loadedData.path);
      if (callback) {
        console.log(callback);
        callback(loadedData.content);
        this.callbacks.delete(loadedData.path);
      }
    });
  }
}

export default ScriptLoader.incetance;