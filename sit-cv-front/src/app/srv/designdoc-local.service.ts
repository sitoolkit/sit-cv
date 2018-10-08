import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { DesignDocIdList } from './designdoc-id-list';
import { DesignDocDetail } from './designdoc-detail';
import { DesignDocLocalRepository } from './designdoc-local.repository';

@Injectable()
export class DesignDocLocalService implements DesignDocService {

  constructor(private repository: DesignDocLocalRepository) {
  }

  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void {
    this.repository.getIdList(callback);
  }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    this.repository.getDetail(designDocId, callback);
  }

}
