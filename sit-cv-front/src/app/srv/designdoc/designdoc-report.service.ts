import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { DesignDocIdList } from './designdoc-id-list';
import { DesignDocDetail } from './designdoc-detail';
import { DesignDocReportRepository } from './designdoc-report.repository';

@Injectable()
export class DesignDocReportService implements DesignDocService {

  constructor(private repository: DesignDocReportRepository) {}

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
