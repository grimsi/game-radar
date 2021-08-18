import {Component, OnInit} from '@angular/core';
import {NavMenuItem} from '../../models/objects/NavMenuItem';
import {Title} from '@angular/platform-browser';
import {Config} from '../../config/Config';
import {Icon} from '../../models/enums/Icon';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';
import {DropDownMenuItem} from "../../models/objects/DropDownMenuItem";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  tabNavItems: NavMenuItem[] = [
    new NavMenuItem('Servers', Icon.dns, '/servers', true),
    new NavMenuItem('Games', Icon.controller, '/games', true),
    new NavMenuItem('Info', Icon.info, '/info', true),
    new NavMenuItem('Config', Icon.settings, '/config', true),
  ];

  dropDownItems: DropDownMenuItem[] = [
    new DropDownMenuItem('Log out', Icon.lock_open, () => {
      this.authService.logout();
    }, true)
  ];

  activeItem: NavMenuItem | undefined;
  userService: UserService;

  constructor(private titleService: Title,
              userService: UserService,
              private authService: AuthService) {
    this.userService = userService;
  }

  ngOnInit() {
  }

  setActiveItem(item: NavMenuItem) {
    this.activeItem = item;
    this.titleService.setTitle(`${Config.baseTitle} - ${item.title}`);
  }
}
