import { Observable } from 'rxjs';

export interface DesignDocService {
  getIdList(): Observable<any>
  getDesignDocDetail(id: string): Observable<any>
}
