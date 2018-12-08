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

import { DesignDocReportRepository } from './srv/designdoc/designdoc-report.repository';
import { DesignDocServerService } from './srv/designdoc/designdoc-server.service';
import { DesignDocReportService } from './srv/designdoc/designdoc-report.service';
import { Config } from './srv/shared/config';
import { HidePackagePipe } from './pipe/hide-package.pipe';
import { ApiDocComponent } from './cmp/designdoc/apidoc/apidoc.component';

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
      useFactory: (repository: DesignDocReportRepository, config: Config) => {
        if (config.isReportMode()) {
          return new DesignDocReportService(repository);
        } else {
          return new DesignDocServerService(config);
        }
      },
      deps: [DesignDocReportRepository, Config]
    },
  ],
  bootstrap: [AppComponent],
  entryComponents :[
    ApiDocComponent
  ]
})
export class AppModule { }
