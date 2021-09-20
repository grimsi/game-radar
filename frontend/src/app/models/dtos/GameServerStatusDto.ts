import "../enums/ServerSt"
import {ServerState} from "../enums/ServerState";

export class GameServerStatusDto {

  // Game-Radar properties
  readonly id!: number;
  name!: string;
  host!: string;
  port!: number;
  game!: string;
  refreshDuration!: string;

  // Game-Server properties
  readonly status!: ServerState;
  readonly currentPlayers!: number;
  readonly maxPlayers!: number;
  readonly currentMap!: string;
  readonly currentGamemode!: string;
  readonly lastRefresh!: Date;
}
