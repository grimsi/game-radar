import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {FullpageLayoutComponent} from "./layouts/fullpage-layout/fullpage-layout.component";
import {NavbarLayoutComponent} from "./layouts/navbar-layout/navbar-layout.component";
import {LoginPageComponent} from "./components/login-page/login-page.component";
import {LogoutPageComponent} from "./components/logout-page/logout-page.component";
import {PageNotFoundComponent} from "./components/page-not-found/page-not-found.component";
import {NotImplementedComponent} from "./components/not-implemented/not-implemented.component";
import {HeaderComponent} from "./components/header/header.component";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatCardModule} from "@angular/material/card";
import {MatProgressButtonsModule} from "mat-progress-buttons";
import {MatTabsModule} from "@angular/material/tabs";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";
import {AppRoutingModule} from "./app-routing.module";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {ErrorInterceptor} from "./interceptor/error.interceptor";
import {ApiUrlInterceptor} from "./interceptor/api-url.interceptor";
import {AuthInterceptor} from "./interceptor/auth.interceptor";
import {ErrorDialogComponent} from "./components/error-dialog/error-dialog.component";
import {MatDialogModule} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {FlexModule} from "@angular/flex-layout";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FullpageLayoutComponent,
    NavbarLayoutComponent,
    LoginPageComponent,
    LogoutPageComponent,
    PageNotFoundComponent,
    NotImplementedComponent,
    ErrorDialogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    MatFormFieldModule,
    MatCardModule,
    MatProgressButtonsModule.forRoot(),
    MatTabsModule,
    MatToolbarModule,
    MatMenuModule,
    MatIconModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    FlexModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ApiUrlInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
