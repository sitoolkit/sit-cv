import Config from '@/infrastructures/Config';
import DesignDocServiceServerImpl from './DesignDocServiceServerImpl';
import DesignDocServiceLocalImpl from './DesignDocServiceLocalImpl';

export default class DesignDocServiceFactory {
  public static getService() {
    return Config.isServerMode ? DesignDocServiceServerImpl : DesignDocServiceLocalImpl;
  }
}
