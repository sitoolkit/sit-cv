import { MenuItem } from '../menu/menu-item';

export interface DesignDocService {
  getMenuList(
    callback: (menuItems: MenuItem[]) => void
  ): void
}
