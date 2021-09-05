import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {InfoApi} from "../api/InfoApi";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class InfoService implements InfoApi {

  private readonly apiPath = '/info';

  constructor(private http: HttpClient) {
  }

  isSetup(): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiPath}/isSetup`);
  }

  version(): Observable<string> {
    return this.http.get(`${this.apiPath}/version`, {responseType: 'text'});
  }

}
