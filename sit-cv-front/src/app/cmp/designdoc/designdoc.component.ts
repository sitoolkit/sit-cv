import { Component, Inject, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material';
import * as $ from 'jquery';
import { DesignDocService } from '../../srv/designdoc/designdoc.service';
import { ApiDocComponent } from './apidoc/apidoc.component';

class Diagram {
  diagram: SafeHtml;
  width: string;
  heightRatio: string;
}

@Component({
  selector: 'app-designdoc',
  templateUrl: './designdoc.component.html',
  styleUrls: ['./designdoc.component.css'],
})

export class DesignDocComponent implements OnInit {
  designDocIds = [];
  currentDesignDocId = '';
  currentDiagrams = [];
  objectKeys = Object.keys;
  diagramApiDocs = {};
  selectedMethodSignatures = [];
  currentMethodSignature = "";
  isDiagramLoading = false;

  constructor(
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    public snackBar: MatSnackBar,
    @Inject('DesignDocService') private ddService: DesignDocService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['designDocId']) {
        this.showDesignDocDetail(params['designDocId']);
      }
    });
  }

  renderDiagrams(diagrams: object) {
    let trustDiagrams: Diagram[] = [];
    Object.keys(diagrams).forEach((key) => {
      let diagram = this.sanitizer.bypassSecurityTrustHtml(diagrams[key]);
      let svg = $(diagrams[key]).filter('svg');
      trustDiagrams.push({
        diagram: diagram,
        width: svg.width() + 'px',
        heightRatio: (svg.height() / svg.width() * 100) + '%',
      });
    });
    this.currentDiagrams = trustDiagrams;
  }

  showDesignDocDetail(designDocId) {
    if (this.currentDesignDocId) {
      this.currentDiagrams = [];
      this.selectedMethodSignatures = [];
    }
    this.currentDesignDocId = designDocId;
    this.isDiagramLoading = true;
    this.ddService.getDetail(designDocId, (detail) => {
      this.isDiagramLoading = false;
      this.renderDiagrams(detail.diagrams);
      this.diagramApiDocs = detail.apiDocs;
    })
    return false;
  }

  methodNameClick(event) {
    let link: JQuery = $(event.target).closest('a');
    if (link.length > 0) {
      this.snackBar.openFromComponent(ApiDocComponent, {
        data: {
          apiDocId: link.attr('xlink:title'),
          apiDocs: this.diagramApiDocs
        }
      });
    }
    return false;
  }

  methodNameMouseover(event) {
    let link: JQuery = $(event.target).closest('a');
    this.currentMethodSignature = link.attr('xlink:title');
    return false;
  }
}
