import DesignDocService from './DesignDocService';
import MenuItem from './MenuItem';
import FunctionModelDetail from './FunctionModelDetail';
import CrudMatrix from './CrudMatrix';
import ScriptLoader from '@/infrastructures/ScriptLoader';
import { AsyncSubject } from 'rxjs';
import Config from '@/infrastructures/Config';

class DesignDocServiceLocalImpl implements DesignDocService {
  private static INSTANDE: DesignDocServiceLocalImpl;

  private detailPathMapSubject: AsyncSubject<boolean> = new AsyncSubject<boolean>();

  private detailPathMap?: DetailPathMap;

  public static get instance() {
    if (!this.INSTANDE) {
      this.INSTANDE = new DesignDocServiceLocalImpl();
      if (!Config.isServerMode) {
        this.INSTANDE.loadFunctionModelDetailPathMap();
      }
    }
    return this.INSTANDE;
  }

  private loadFunctionModelDetailPathMap() {
    ScriptLoader.load('functionmodel/detail-path-map.js', (detailPathMap: DetailPathMap) => {
      this.detailPathMap = detailPathMap;
      this.detailPathMapSubject.next(true);
      this.detailPathMapSubject.complete();
    });
  }

  public fetchMenuItems(callback: (menuItems: MenuItem[]) => void): void {
    ScriptLoader.load('assets/designdoc-list.js', callback);
  }

  public fetchFunctionModelDetail(
    functionId: string,
    callback: (funcionModelDetail: FunctionModelDetail) => void
  ): void {
    this.detailPathMapSubject.subscribe(() => {
      ScriptLoader.load(this.detailPathMap![functionId], (detailMap: DetailMap) => {
        callback(detailMap.detailMap[functionId]);
      });
    });
  }

  public async getCrudModel() {
    return new Promise<CrudMatrix>((resolve) => {
      ScriptLoader.load('datamodel/crud/crud.js', (crudMatrix) => {
        resolve(crudMatrix);
      });
    });
  }
}

type DetailPathMap = { [id: string]: string };

interface DetailMap {
  detailMap: { [id: string]: FunctionModelDetail };
}

export default DesignDocServiceLocalImpl.instance;
