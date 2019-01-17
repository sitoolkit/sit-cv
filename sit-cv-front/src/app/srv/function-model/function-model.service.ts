import { DesignDocDetail } from '../designdoc/designdoc-detail';

export interface FunctionModelService {
  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void
}