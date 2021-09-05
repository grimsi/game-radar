import {Component, OnInit} from '@angular/core';
import {MatProgressButtonOptions} from "mat-progress-buttons";
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {UserService} from "../../services/user.service";
import {ApiErrorResponse} from "../../models/dtos/ApiErrorResponse";
import {Config} from "../../config/Config";
import {InfoService} from "../../services/info.service";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-setup-page',
  templateUrl: './setup-page.component.html',
  styleUrls: ['./setup-page.component.scss']
})
export class SetupPageComponent implements OnInit {

  version: string = "Loading...";

  returnUrl: string = "";
  error: string = "";

  username: string = "";
  password: string = "";
  confirmPassword: string = "";
  email: string = "";

  setupButton: MatProgressButtonOptions = {
    active: false,
    text: 'Login',
    spinnerSize: 18,
    raised: true,
    stroked: false,
    buttonColor: 'primary',
    spinnerColor: 'accent',
    fullWidth: false,
    disabled: true,
    mode: 'indeterminate',
  };

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private infoService: InfoService
  ) {
  }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['']);
    }
    this.infoService.version().subscribe(
      (backendVersion: string) => {
        this.version = backendVersion;
      },
      (error: any) => {
        this.version = environment.VERSION;
      }
    );
  }

  public setup(): void {
    this.setupButton.active = true;

    this.authService.setup(this.username, this.password, this.email)
      .subscribe(
        (response: any) => {
          this.setupButton.active = false;
          this.router.navigate(['']);
        },
        (error: ApiErrorResponse) => {
          this.setupButton.active = false;

          switch (error.status) {
            case 404:
              this.error = 'The application has already been set up.<br/>Redirecting you to the login...';
              setTimeout(
                () => {
                  this.router.navigate(['/login']);
                },
                Config.logoutRedirectTimeout
              );
              break;
            default:
              this.error = 'Error while trying to setup application.';
              break;
          }
        });
  }

}
