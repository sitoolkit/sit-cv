import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { MY_ROUTES } from './app.routing';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavbarComponent } from './navbar/navbar.component';
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
import { DesignDocComponent } from './designdoc/designdoc.component';
import { ErrorComponent } from './error.component';
import { DoctreeComponent } from './doctree/doctree.component';

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
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
