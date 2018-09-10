import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { MY_ROUTES } from './app.routing';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavbarComponent } from './navbar/navbar.component';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule, MatButtonModule, MatSidenavModule, MatIconModule, MatListModule } from '@angular/material';
import { UmlComponent } from './uml/uml.component';
import { ErrorComponent } from './error.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    UmlComponent,
    ErrorComponent,
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
    MY_ROUTES,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
