import {Observable} from "rxjs";
import {GameServerDto} from "../models/dtos/GameServerDto";
import {GameServerStatusDto} from "../models/dtos/GameServerStatusDto";

export interface GameServerApi {
  getGameServers(host?: string): Observable<GameServerDto[]>;

  getGameServerStatuses(host?: string): Observable<GameServerStatusDto[]>;

  createGameServer(gameServer: GameServerDto): Observable<GameServerDto>;

  editGameServerById(gameServer: GameServerDto): Observable<GameServerDto>;

  deleteGameServerById(gameServerId: number): Observable<Response>;
}
