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
  MatProgressSpinnerModule,
  MatCardModule
} from '@angular/material';
import { MatTreeModule } from '@angular/material/tree';
import { DesignDocComponent } from './cmp/designdoc/designdoc.component';
import { ErrorComponent } from './error.component';
import { DoctreeComponent } from './cmp/doctree/doctree.component';

import { DesignDocLocalRepository } from './srv/designdoc/designdoc-local.repository';
import { DesignDocWebsocketService } from './srv/designdoc/designdoc-websocket.service';
import { DesignDocLocalService } from './srv/designdoc/designdoc-local.service';

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
    MY_ROUTES,
  ],
  providers: [
    {
      provide: 'DesignDocService',
      useFactory: (repository: DesignDocLocalRepository) => {
        if (repository.isReady()) {
          return new DesignDocLocalService(repository);
        } else {
          return new DesignDocWebsocketService();
        }
      },
      deps: [DesignDocLocalRepository]
    },
    DesignDocLocalRepository
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
