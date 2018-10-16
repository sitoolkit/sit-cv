import { ModuleWithProviders } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppComponent} from './app.component';

const routes: Routes = [
  {path: '', component: AppComponent},
  {path: '**', redirectTo: '', pathMatch: 'full' },
];

export const MY_ROUTES: ModuleWithProviders = RouterModule.forRoot(routes);