import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

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
  MatProgressSpinnerModule,
  MatCardModule
} from '@angular/material';
import { MatTreeModule } from '@angular/material/tree';
import { DesignDocComponent } from './cmp/designdoc/designdoc.component';
import { ErrorComponent } from './error.component';
import { DoctreeComponent } from './cmp/doctree/doctree.component';

import { DesignDocReportRepository } from './srv/designdoc/designdoc-report.repository';
import { DesignDocServerService } from './srv/designdoc/designdoc-server.service';
import { DesignDocReportService } from './srv/designdoc/designdoc-report.service';
import { Config } from './srv/shared/config';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    DesignDocComponent,
    ErrorComponent,
    DoctreeComponent,
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
    MatProgressSpinnerModule,
    MatCardModule,
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
  bootstrap: [AppComponent]
})
export class AppModule { }
