import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {UserService} from '../../services/user.service';
import {ApiErrorResponse} from '../../models/dtos/ApiErrorResponse';
import {environment} from '../../../environments/environment';

@Component({
  templateUrl: 'login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent implements OnInit {

  version: string = environment.version;

  returnUrl: string = "";
  error: string = "";

  username: string = "";
  password: string = "";

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private userService: UserService
  ) {
  }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['']);
    }
  }

  public login(): void {
    this.authService.login(this.username, this.password)
      .subscribe(
        (response: any) => {
          const token = response.headers.get('Authorization');
          const returnUrl: string = this.route.snapshot.queryParamMap.get('returnUrl') || '';
          this.authService.setToken(token);
          this.userService.userName = this.username;
          this.router.navigate([returnUrl]);
        },
        (error: ApiErrorResponse) => {
          if (error.status === 401) {
            this.error = `The username or password you entered is incorrect.`;
          }
        });
  }
}
