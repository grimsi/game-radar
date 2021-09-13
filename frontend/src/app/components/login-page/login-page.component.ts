import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {UserService} from '../../services/user.service';
import {ApiErrorResponse} from '../../models/dtos/ApiErrorResponse';
import {MatProgressButtonOptions} from 'mat-progress-buttons';
import {InfoService} from "../../services/info.service";

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent implements OnInit {

  version: string = "Loading...";

  returnUrl: string = "";
  error: string = "";

  username: string = "";
  password: string = "";

  loginButton: MatProgressButtonOptions = {
    active: false,
    text: 'Login',
    spinnerSize: 18,
    raised: true,
    stroked: false,
    buttonColor: 'primary',
    spinnerColor: 'accent',
    fullWidth: false,
    disabled: false,
    mode: 'indeterminate',
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private infoService: InfoService
  ) {
  }

  async ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router
        .navigateByUrl('')
        .catch(e => console.error(e));
    }

    this.version = await this.infoService.version();
  }

  public login(): void {
    this.error = "";
    this.loginButton.active = true;

    this.authService.login(this.username, this.password)
      .subscribe(
        (response: any) => {
          this.loginButton.active = false;
          const token = response.headers.get('Authorization');
          const returnUrl: string = this.route.snapshot.queryParamMap.get('returnUrl') || '';
          this.authService.setToken(token);
          this.userService.userName = this.username;
          this.router
            .navigateByUrl(returnUrl)
            .catch(e => console.error(e));
        },
        (error: ApiErrorResponse) => {
          this.loginButton.active = false;
          if (error.status === 401) {
            this.error = `The username or password you entered is incorrect.`;
          }
        });
  }
}
