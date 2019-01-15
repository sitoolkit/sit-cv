import { DesignDocDetail } from './designdoc-detail';
import { DesignDocIdList } from './designdoc-id-list';
import { DesignDocMenuItem } from './designdoc-menu-item';

export interface DesignDocService {
  getMenuList(
    callback: (menuItems: DesignDocMenuItem[]) => void
  ): void
  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void
  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void
}
