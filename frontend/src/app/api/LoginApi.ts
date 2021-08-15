import {Observable} from 'rxjs';
import {HttpResponse} from '@angular/common/http';

export interface LoginApi {
  login(username: string, password: string): Observable<HttpResponse<Response>>;
}
