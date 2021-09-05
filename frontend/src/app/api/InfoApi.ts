import {Observable} from 'rxjs';

export interface InfoApi {
  isSetup(): Observable<boolean>;

  version(): Observable<string>;
}
