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
  MatSnackBarModule
} from '@angular/material';
import { MatTreeModule } from '@angular/material/tree';
import { FunctionModelComponent } from './cmp/function-model/function-model.component';
import { ErrorComponent } from './error.component';
import { DoctreeComponent } from './cmp/doctree/doctree.component';

import { DesignDocServerService } from './srv/designdoc/designdoc-server.service';
import { DesignDocReportService } from './srv/designdoc/designdoc-report.service';
import { Config } from './srv/shared/config';
import { HidePackagePipe } from './pipe/hide-package.pipe';
import { ApiDocComponent } from './cmp/function-model/apidoc/apidoc.component';
import { SitCvWebsocket } from './srv/shared/sit-cv-websocket';
import { FunctionModelReportService } from './srv/function-model/function-model-report.service';
import { FunctionModelServerService } from './srv/function-model/function-model-server.service';
import { ReportDataLoader } from './srv/shared/report-data-loader';
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
    MY_ROUTES,
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
