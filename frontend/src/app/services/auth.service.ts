import {Injectable} from '@angular/core';
import {LoginApi} from '../api/LoginApi';
import {AuthBody} from '../models/objects/AuthBody';
import {Observable} from 'rxjs';
import {JwtHelperService} from '@auth0/angular-jwt';
import {Router} from '@angular/router';
import {UserService} from './user.service';
import {HttpClient, HttpResponse} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AuthService implements LoginApi {

  private readonly apiPath = '/login';
  private readonly tokenStorageKey = 'token';
  private readonly jwt = new JwtHelperService();

  constructor(private http: HttpClient,
              private router: Router,
              private userService: UserService) {
  }

  public getToken(): string {
    return <string>localStorage.getItem(this.tokenStorageKey);
  }

  public setToken(tokenHeader: string): void {
    const token = tokenHeader.substring(7);
    localStorage.setItem(this.tokenStorageKey, token);
  }

  public isAuthenticated(): boolean {
    return !this.jwt.isTokenExpired(this.getToken());
  }

  public login(username: string, password: string): Observable<HttpResponse<Response>> {
    return this.http.post<Response>(this.apiPath, new AuthBody(username, password), {observe: 'response'});
  }

  public setup(username: string, password: string, email: string) {
    return this.http.post('/setup', {username: username, password: password, email: email}, {observe: 'response'});
  }

  public logout(redirect: boolean = true): void {
    if (redirect) {
      this.router
        .navigateByUrl('/logout')
        .catch(e => console.error(e));
    }
    localStorage.removeItem(this.tokenStorageKey);
    this.userService.removeKey();
  }
}
