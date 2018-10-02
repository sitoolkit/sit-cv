import { Observable } from 'rxjs';

export interface DesignDocService {
  getDesignDocIdList(): Observable<any>
  getDesignDocDetail(id: string): Observable<any>
}
