import { ModuleWithProviders } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {FunctionModelComponent} from './cmp/function-model/function-model.component';

const routes: Routes = [
  { path: 'designdoc/function/:functionId', component: FunctionModelComponent },
  { path: '**', redirectTo: '', pathMatch: 'full' },
];

export const MY_ROUTES: ModuleWithProviders = RouterModule.forRoot(routes, { useHash: true });