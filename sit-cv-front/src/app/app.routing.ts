import { ModuleWithProviders } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {FunctionModelComponent} from './cmp/function-model/function-model.component';
import { CrudComponent } from './cmp/data-model/crud/crud.component';

const routes: Routes = [
  { path: 'designdoc/function/:functionId', component: FunctionModelComponent },
  { path: 'designdoc/data/crud', component: CrudComponent },
  { path: '**', redirectTo: '', pathMatch: 'full' },
];

export const MY_ROUTES: ModuleWithProviders = RouterModule.forRoot(routes, { useHash: true });