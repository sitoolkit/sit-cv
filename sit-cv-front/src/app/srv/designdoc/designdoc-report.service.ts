import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { DesignDocDetail } from './designdoc-detail';
import { DesignDocReportRepository } from './designdoc-report.repository';
import { MenuItem } from '../menu/menu-item';

@Injectable()
export class DesignDocReportService implements DesignDocService {

  constructor(private repository: DesignDocReportRepository) {}

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    this.repository.getDetail(designDocId, callback);
  }

  getMenuList(
    callback: (menuItems: MenuItem[]) => void
  ): void {
  }

}
