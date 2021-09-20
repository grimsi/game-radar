export class GameServerDto {
  readonly id?: number;
  name!: string;
  host!: string;
  port?: number;
  game!: string;
  refreshDuration?: string;
}
