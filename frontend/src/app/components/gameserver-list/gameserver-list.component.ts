import {AfterViewInit, Component} from '@angular/core';
import {GameserverApiService} from "../../services/gameserver-api.service";
import {GameServerStatusDto} from "../../models/dtos/GameServerStatusDto";

@Component({
  selector: 'app-gameserver-list',
  templateUrl: './gameserver-list.component.html',
  styleUrls: ['./gameserver-list.component.css']
})
export class GameserverListComponent implements AfterViewInit {

  gameServers: GameServerStatusDto[] = [];
  loading: boolean = true;

  constructor(private gameServerService: GameserverApiService) {
  }

  ngAfterViewInit(): void {
    this.gameServerService.getGameServerStatuses().subscribe(
      (servers: GameServerStatusDto[]) => {
        this.gameServers = servers;
        this.loading = false;
      }
    );
  }

}
