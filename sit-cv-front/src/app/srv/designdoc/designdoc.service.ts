import { DesignDocDetail } from './designdoc-detail';
import { DesignDocIdList } from './designdoc-id-list';

export interface DesignDocService {
  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void
  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void
}
