import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {InfoApi} from "../api/InfoApi";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class InfoService implements InfoApi, OnInit {

  private readonly apiPath = '/info';

  private isSetupCachedValue: boolean = false;
  private versionCachedValue: string | null = null;

  constructor(private http: HttpClient) {
  }

  async ngOnInit(): Promise<void> {
    // cache the "isSetup" value to minimize REST API calls since it's used for the auth guards
    this.isSetupCachedValue = await this.isSetupFromBackend();

    // cache the "version" value since the version is unlikely to change and even then,
    // a simple page refresh will reload the correct version
    this.versionCachedValue = await this.versionFromBackend();
  }

  async isSetup(): Promise<boolean> {
    // if the application has already been setup, it's impossible to go into "not setup" stage again
    // so we can skip the REST API call here and just return "true"
    if (this.isSetupCachedValue) {
      return true;
    }

    // if it hasn't been set up before, check the REST API and return the current setup state
    this.isSetupCachedValue = await this.isSetupFromBackend();
    return this.isSetupCachedValue;
  }

  async version(): Promise<string> {

    // if the version has not been retrieved yet, fetch it from the backend and cache it
    if (this.versionCachedValue === null) {
      try {
        this.versionCachedValue = await this.versionFromBackend();
        return this.versionCachedValue;
      } catch (error) {
        // return the one from the environment as fallback
        return environment.VERSION;
      }
    }

    return this.versionCachedValue;
  }

  private isSetupFromBackend(): Promise<boolean> {
    return this.http.get<boolean>(`${this.apiPath}/isSetup`).toPromise();
  }

  private versionFromBackend(): Promise<string> {
    return this.http.get(`${this.apiPath}/version`, {responseType: 'text'}).toPromise();
  }

}
