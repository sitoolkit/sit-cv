import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { DesignDocIdList } from './designdoc-id-list';
import { DesignDocDetail } from './designdoc-detail';
import { DesignDocReportRepository } from './designdoc-report.repository';
import { AsyncSubject } from 'rxjs';

@Injectable()
export class DesignDocReportService implements DesignDocService {

  private idListSubject: AsyncSubject<boolean> = new AsyncSubject<boolean>();

  constructor(private repository: DesignDocReportRepository) {}

  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void {
    this.repository.getIdList((idList: DesignDocIdList) => {
      callback(idList);
      this.idListSubject.next(true);
      this.idListSubject.complete();
    });
  }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    this.idListSubject.subscribe(() => {
      this.repository.getDetail(designDocId, callback);
    })
  }

}
