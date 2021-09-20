import {Injectable} from '@angular/core';
import {GameServerApi} from "../api/GameServerApi";
import {HttpClient, HttpParams} from "@angular/common/http";
import {GameServerDto} from "../models/dtos/GameServerDto";
import {Observable} from "rxjs";
import {GameServerStatusDto} from "../models/dtos/GameServerStatusDto";

@Injectable({
  providedIn: 'root'
})
export class GameserverApiService implements GameServerApi {

  private readonly apiPath = '/gameservers';

  constructor(private http: HttpClient) {
  }

  createGameServer(gameServer: GameServerDto): Observable<GameServerDto> {
    return this.http.post<GameServerDto>(this.apiPath, gameServer);
  }

  deleteGameServerById(gameServerId: number): Observable<Response> {
    return this.http.delete<Response>(`${this.apiPath}/${gameServerId}`);
  }

  editGameServerById(gameServer: GameServerDto): Observable<GameServerDto> {
    return this.http.patch<GameServerDto>(`${this.apiPath}/${gameServer.id}`, gameServer);
  }

  getGameServerStatuses(host?: string): Observable<GameServerStatusDto[]> {
    let requestParameters: HttpParams = new HttpParams();

    if (host !== undefined) {
      requestParameters.set("host", host);
    }

    return this.http.get<GameServerStatusDto[]>(`${this.apiPath}/status`, {params: requestParameters});
  }

  getGameServers(host?: string): Observable<GameServerDto[]> {
    let requestParameters: HttpParams = new HttpParams();

    if (host !== undefined) {
      requestParameters.set("host", host);
    }

    return this.http.get<GameServerDto[]>(this.apiPath, {params: requestParameters});
  }
}
