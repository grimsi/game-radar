import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {InfoService} from "../services/info.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService,
              private infoService: InfoService,
              private router: Router) {
  }

  async canActivate(next: ActivatedRouteSnapshot,
                    state: RouterStateSnapshot): Promise<boolean> {

    if (!await this.infoService.isSetup()) {

      this.router
        .navigateByUrl('/setup')
        .catch(e => console.error(e));

      return false;
    }

    if (this.authService.isAuthenticated()) {

      return true;

    } else {

      this.router
        .navigate(['login'], {queryParams: {returnUrl: window.location.pathname}})
        .catch(e => console.error(e));

      return false;
    }
  }
}
