import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DesignDocComponent } from '../designdoc/designdoc.component';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  currentDesignDocId = '';

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches)
    );

  constructor(private route: ActivatedRoute,
    private breakpointObserver: BreakpointObserver) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['designDocId']) {
        this.currentDesignDocId=params['designDocId'];
      }
    });
  }
}

