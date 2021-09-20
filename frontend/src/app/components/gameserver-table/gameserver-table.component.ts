import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {GameserverApiService} from "../../services/gameserver-api.service";
import {GameServerStatusDto} from "../../models/dtos/GameServerStatusDto";
import {MatTable, MatTableDataSource} from "@angular/material/table";
import {MatSort} from "@angular/material/sort";
import {MatPaginator} from "@angular/material/paginator";

@Component({
  selector: 'gameserver-table',
  templateUrl: './gameserver-table.component.html',
  styleUrls: ['./gameserver-table.component.css']
})
export class GameserverTableComponent implements AfterViewInit {
  @ViewChild(MatPaginator) paginator: MatPaginator | undefined;
  @ViewChild(MatSort) sort: MatSort | undefined;
  @ViewChild(MatTable) gameServerTable!: MatTable<GameServerStatusDto>;

  @Input()
  data!: GameServerStatusDto[];

  dataSource: MatTableDataSource<GameServerStatusDto> = new MatTableDataSource<GameServerStatusDto>();

  constructor(private gameserverApiService: GameserverApiService) {
  }


  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
    // tslint:disable-next-line:max-line-length
  displayedColumns = ['name', 'game', 'map', 'players', 'status', 'address', 'lastRefresh'];

  ngAfterViewInit(): void {
    this.dataSource.paginator = <MatPaginator>this.paginator;
    this.dataSource.sort = <MatSort>this.sort;
    this.dataSource.data = this.data;
  }

  refresh(): void {
    this.gameServerTable.renderRows();
  }

  createServer(): void {
    // this.dialogService.showCreateServerDialog();
  }

  editServer(server: GameServerStatusDto): void {
    // this.dialogService.showEditServerDialog(server);
  }

  deleteServer(server: GameServerStatusDto): void {
    this.gameserverApiService.deleteGameServerById(server.id).subscribe();
  }
}
