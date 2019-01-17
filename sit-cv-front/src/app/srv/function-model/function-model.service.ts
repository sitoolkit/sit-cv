import { FunctionModelDetail } from './function-model-detail';

export interface FunctionModelService {
  getDetail(
    functionId: string,
    callback: (detail: FunctionModelDetail) => void
  ): void
}