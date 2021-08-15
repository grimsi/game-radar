import {Injectable, OnInit} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService implements OnInit {

  private readonly userNameKey = 'username';

  private _userName: string | undefined;

  constructor() {
  }

  ngOnInit(): void {
    const username: string | null = localStorage.getItem(this.userNameKey);
    if (username !== null) {
      this.userName = username;
    }
  }

  get userName(): string {
    return <string>this._userName;
  }

  set userName(value: string) {
    localStorage.setItem(this.userNameKey, value);
    this._userName = value;
  }

  public removeKey(): void {
    localStorage.removeItem(this.userNameKey);
  }
}
