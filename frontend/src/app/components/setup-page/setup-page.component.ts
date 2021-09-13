import {Component, OnInit} from '@angular/core';
import {MatProgressButtonOptions} from "mat-progress-buttons";
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {UserService} from "../../services/user.service";
import {ApiErrorResponse} from "../../models/dtos/ApiErrorResponse";
import {Config} from "../../config/Config";
import {InfoService} from "../../services/info.service";

@Component({
  selector: 'app-setup-page',
  templateUrl: './setup-page.component.html',
  styleUrls: ['./setup-page.component.scss']
})
export class SetupPageComponent implements OnInit {

  version: string = "Loading...";

  error: string = "";

  username: string = "";
  password: string = "";
  confirmPassword: string = "";
  email: string = "";

  setupButton: MatProgressButtonOptions = {
    active: false,
    text: 'Finish setup',
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

  async ngOnInit() {

    if (await this.infoService.isSetup()) {
      this.router
        .navigate([''])
        .catch(e => console.error(e));
    }

    this.infoService.version().then((version: string) => this.version = version);
  }

  public setup(): void {
    this.error = "";
    this.setupButton.active = true;

    this.authService.setup(this.username, this.password, this.email)
      .subscribe(
        () => {
          this.setupButton.active = false;
          this.router
            .navigate([''])
            .catch(e => console.error(e));
        },
        (error: ApiErrorResponse) => {
          this.setupButton.active = false;

          switch (error.status) {
            case 404:
              this.error = 'The application has already been set up.<br/>Redirecting you to the login...';
              setTimeout(
                () => {
                  this.router
                    .navigate(['/login'])
                    .catch(e => console.error(e));
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
