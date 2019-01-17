import { Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router, RoutesRecognized } from '@angular/router';
import { FunctionModelComponent } from '../function-model/function-model.component';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  subtitle: string

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches)
    );

  constructor(
    private breakpointObserver: BreakpointObserver,
    private router: Router
  ) { }

  ngOnInit() {
    this.router.events.subscribe(event => {
      if (event instanceof RoutesRecognized) {
        let componentType = event.state.root.firstChild.component;
        let params = event.state.root.firstChild.params;
        switch (componentType) {
          case FunctionModelComponent:
            this.subtitle = params['functionId'];
            break;
          default:
            this.subtitle = null;
            break;
        }
      }
    });
  }

}

