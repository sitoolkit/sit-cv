import { Injectable } from '@angular/core';
import { DesignDocService } from './designdoc.service';
import { DesignDocIdList } from './designdoc-id-list';
import { DesignDocDetail } from './designdoc-detail';

@Injectable()
export class DesignDocLocalService implements DesignDocService {

  getIdList(
    callback: (idList: DesignDocIdList) => void
  ): void {
    let idList = new DesignDocIdList();
    idList.ids = Object.keys((<any>window).designDocsData.idList);
    callback(idList);
  }

  getDetail(
    designDocId: string,
    callback: (detail: DesignDocDetail) => void
  ): void {
    if ((<any>window).designDocsData.detailList[designDocId] == null) {
      let script = document.createElement("script");
      script.onload = () => {
        let detail = (<DesignDocDetail>(<any>window).designDocsData.detailList[designDocId]);
        callback(detail);
      }
      script.src = (<any>window).designDocsData.idList[designDocId];
      document.body.appendChild(script);
    } else {
      let detail = (<DesignDocDetail>(<any>window).designDocsData.detailList[designDocId]);
      callback(detail);
    }
  }

}
