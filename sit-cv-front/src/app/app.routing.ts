import { ModuleWithProviders } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppComponent} from './app.component';
import {NavbarComponent} from './cmp/navbar/navbar.component';
import {DesignDocComponent} from './cmp/designdoc/designdoc.component';

const routes: Routes = [
  {path: 'designdoc/function/:designDocId', component: NavbarComponent},
  {path: '**', redirectTo: 'designdoc/function/', pathMatch: 'full' },
];

export const MY_ROUTES: ModuleWithProviders = RouterModule.forRoot(routes, { useHash: true });