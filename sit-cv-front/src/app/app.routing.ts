import { ModuleWithProviders }   from '@angular/core';
import { RouterModule }   from '@angular/router';

import { UmlComponent }  from './uml/uml.component';
import { ErrorComponent } from './error.component';

const myRoutes = [
  { path: '', component: UmlComponent },
  { path: '**', component: ErrorComponent }
];

export const MY_ROUTES: ModuleWithProviders = RouterModule.forRoot(myRoutes);