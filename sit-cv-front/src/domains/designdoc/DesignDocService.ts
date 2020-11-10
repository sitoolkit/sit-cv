import MenuItem from './MenuItem';
import FunctionModelDetail from './FunctionModelDetail';
import CrudMatrix from './CrudMatrix';

export default interface DesignDocService {
  fetchMenuItems(callback: (menuItems: MenuItem[]) => void): void;

  fetchFunctionModelDetail(
    functionId: string,
    callback: (funcionModelDetail: FunctionModelDetail) => void
  ): void;

  getCrudModel(): Promise<CrudMatrix>;

  getMenuItems(): MenuItem[];
}
