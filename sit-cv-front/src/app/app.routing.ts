import { ModuleWithProviders }   from '@angular/core';
import { RouterModule }   from '@angular/router';

import { DesignDocComponent } from './designdoc/designdoc.component';
import { ErrorComponent } from './error.component';

const routes = [
  { path: '', redirectTo: 'designdoc', pathMatch: 'full' },
  { path: 'designdoc', component: DesignDocComponent },
  { path: '**', component: ErrorComponent }
];

export const MY_ROUTES: ModuleWithProviders = RouterModule.forRoot(routes, { useHash: true });