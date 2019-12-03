import Config from './Config';

class ScriptLoader {
  private static INSTANCE: ScriptLoader;

  private callbacks = new Map<string, Array<(data:any) => void>>();

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

    if (!this.callbacks.has(scriptPath)) {
      this.callbacks.set(scriptPath, []);
    }
    this.callbacks.get(scriptPath)!.push(callback);
  }

  setMessageListener() {
    addEventListener("message", (event) => {
      if (event.source != window) {
        return;
      } 

      const loadedData = event.data;
      console.log("Receive postMessage ", loadedData);

      const callbacks = this.callbacks.get(loadedData.path);
      if (callbacks) {
        callbacks.forEach((callback) => {
          console.log(callback);
          callback(loadedData.content);
        });
        this.callbacks.delete(loadedData.path);
      }
    });
  }
}

export default ScriptLoader.incetance;