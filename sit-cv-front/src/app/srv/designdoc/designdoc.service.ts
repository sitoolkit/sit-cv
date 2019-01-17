import { DesignDocDetail } from './designdoc-detail';
import { MenuItem } from '../menu/menu-item';

export interface DesignDocService {
  getMenuList(
    callback: (menuItems: MenuItem[]) => void
  ): void
  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void
}
