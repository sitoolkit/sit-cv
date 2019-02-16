import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { MenuItem } from '../menu/menu-item';
import { ReportDataLoader } from '../shared/report-data-loader';

@Injectable()
export class DesignDocReportService implements DesignDocService {

  constructor(private reportLoader: ReportDataLoader) {}

  getMenuList(
    callback: (menuItems: MenuItem[]) => void
  ): void {
    this.reportLoader.loadScript("assets/designdoc-list.js", (menuItems) => {
      callback(menuItems);
    })
  }

}
