import { FunctionModelDetail } from './function-model-detail';

export interface FunctionModelService {
  getDetail(
    designDocId: string,
    callback: (detail: FunctionModelDetail) => void
  ): void
}