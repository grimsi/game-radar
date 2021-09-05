import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthGuard} from "./guards/auth.guard";
import {FullpageLayoutComponent} from "./layouts/fullpage-layout/fullpage-layout.component";
import {LoginPageComponent} from "./components/login-page/login-page.component";
import {LogoutPageComponent} from "./components/logout-page/logout-page.component";
import {PageNotFoundComponent} from "./components/page-not-found/page-not-found.component";
import {NavbarLayoutComponent} from "./layouts/navbar-layout/navbar-layout.component";
import {NotImplementedComponent} from "./components/not-implemented/not-implemented.component";
import {SetupPageComponent} from "./components/setup-page/setup-page.component";

const appRoutes: Routes = [
  {
    path: '',
    component: NavbarLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'servers',
        component: NotImplementedComponent
      },
      {
        path: 'games',
        component: NotImplementedComponent
      },
      {
        path: 'info',
        component: NotImplementedComponent
      },
      {
        path: 'config',
        component: NotImplementedComponent
      },
      {
        path: '',
        redirectTo: '/servers',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '',
    component: FullpageLayoutComponent,
    children: [
      {
        path: 'setup',
        component: SetupPageComponent
      },
      {
        path: 'login',
        component: LoginPageComponent
      },
      {
        path: 'logout',
        component: LogoutPageComponent
      },
      {
        path: '',
        redirectTo: '/setup',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    component: PageNotFoundComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes)],
  exports: [RouterModule]
})

export class AppRoutingModule {
}
