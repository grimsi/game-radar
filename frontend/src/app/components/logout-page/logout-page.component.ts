import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Config} from 'src/app/config/Config';

@Component({
  templateUrl: './logout-page.component.html',
  styleUrls: ['./logout-page.component.scss']
})
export class LogoutPageComponent implements OnInit {

  constructor(private router: Router) {
  }

  ngOnInit() {
    setTimeout(
      () => {
        this.router
          .navigateByUrl('/login')
          .catch(e => console.error(e));
      },
      Config.logoutRedirectTimeout
    );
  }

}
