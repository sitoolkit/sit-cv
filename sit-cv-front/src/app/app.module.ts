import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { MY_ROUTES } from './app.routing';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavbarComponent } from './cmp/navbar/navbar.component';
import { LayoutModule } from '@angular/cdk/layout';
import {
  MatToolbarModule,
  MatButtonModule,
  MatSidenavModule,
  MatIconModule,
  MatListModule,
  MatProgressBarModule,
  MatCardModule,
  MatSnackBarModule,
  MatTableModule
} from '@angular/material';
import { MatTreeModule } from '@angular/material/tree';
import { FunctionModelComponent } from './cmp/function-model/function-model.component';
import { ErrorComponent } from './error.component';
import { DoctreeComponent } from './cmp/doctree/doctree.component';

import { Config } from './srv/shared/config';
import { HidePackagePipe } from './pipe/hide-package.pipe';
import { ApiDocComponent } from './cmp/function-model/apidoc/apidoc.component';
import { SitCvWebsocket } from './srv/shared/sit-cv-websocket';
import { ReportDataLoader } from './srv/shared/report-data-loader';
import { CrudComponent } from './cmp/data-model/crud/crud.component';
import { HttpModule } from '@angular/http';
import { ServiceFactory } from './service-factory';

let serviceFactory = new ServiceFactory();

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FunctionModelComponent,
    ErrorComponent,
    DoctreeComponent,
    HidePackagePipe,
    ApiDocComponent,
    CrudComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatTreeModule,
    MatProgressBarModule,
    MatCardModule,
    MatSnackBarModule,
    MatTableModule,
    MY_ROUTES,
    HttpModule,
  ],
  providers: [
    {
      provide: 'DesignDocService',
      useFactory: serviceFactory.createDesignDocService,
      deps: [ReportDataLoader, SitCvWebsocket, Config]
    },
    {
      provide: 'FunctionModelService',
      useFactory: serviceFactory.createFunctionModelService,
      deps: [ReportDataLoader, SitCvWebsocket, Config]
    },
  ],
  bootstrap: [AppComponent],
  entryComponents :[
    ApiDocComponent
  ]
})
export class AppModule { }
