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
import { DesignDocComponent } from './cmp/designdoc/designdoc.component';
import { ErrorComponent } from './error.component';
import { DoctreeComponent } from './cmp/doctree/doctree.component';

import { DesignDocServerService } from './srv/designdoc/designdoc-server.service';
import { DesignDocReportService } from './srv/designdoc/designdoc-report.service';
import { Config } from './srv/shared/config';
import { HidePackagePipe } from './pipe/hide-package.pipe';
import { ApiDocComponent } from './cmp/designdoc/apidoc/apidoc.component';
import { SitCvWebsocket } from './srv/shared/sit-cv-websocket';
import { FunctionModelReportService } from './srv/function-model/function-model-report.service';
import { FunctionModelServerService } from './srv/function-model/function-model-server.service';
import { ReportDataLoader } from './srv/shared/report-data-loader';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    DesignDocComponent,
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
      useFactory: (reportLoader: ReportDataLoader, socket: SitCvWebsocket, config: Config) => {
        if (config.isReportMode()) {
          return new DesignDocReportService(reportLoader);
        } else {
          return new DesignDocServerService(socket);
        }
      },
      deps: [ReportDataLoader, SitCvWebsocket, Config]
    },
    {
      provide: 'FunctionModelService',
      useFactory: (reportLoader: ReportDataLoader, socket: SitCvWebsocket, config: Config) => {
        if (config.isReportMode()) {
          return new FunctionModelReportService(reportLoader);
        } else {
          return new FunctionModelServerService(socket);
        }
      },
      deps: [ReportDataLoader, SitCvWebsocket, Config]
    },
  ],
  bootstrap: [AppComponent],
  entryComponents :[
    ApiDocComponent
  ]
})
export class AppModule { }
