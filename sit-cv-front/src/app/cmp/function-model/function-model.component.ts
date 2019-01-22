import { Component, Inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material';
import * as $ from 'jquery';
import { ApiDocComponent } from './apidoc/apidoc.component';
import { trigger, style, transition, animate } from '@angular/animations';
import { FunctionModelService } from 'src/app/srv/function-model/function-model.service';

interface Diagram {
  diagram: SafeHtml;
  width: string;
  heightRatio: string;
}

interface DiagramGroup {
  diagrams: Diagram[];
}

@Component({
  selector: 'app-function-model',
  templateUrl: './function-model.component.html',
  styleUrls: ['./function-model.component.css'],
  animations: [
    trigger('diagramAnimation', [
      transition(":enter", [
        style({ opacity: 0 }),
        animate(500, style({ opacity: 1 }))
      ]),
      transition("leaveEnable => void", [
        animate(300, style({ opacity: 0 }))
      ])
    ]),
  ],
})

export class FunctionModelComponent implements OnInit {
  currentFunctionId = '';
  currentDiagramGroups: DiagramGroup[] = [];
  objectKeys = Object.keys;
  diagramApiDocs = {};
  selectedMethodSignatures = [];
  currentMethodSignature = "";
  isDiagramLoading = false;
  isLeaveAnimationEnabled = true;

  constructor(
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    public snackBar: MatSnackBar,
    private chRef: ChangeDetectorRef,
    @Inject('FunctionModelService') private functionModelService: FunctionModelService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['functionId']) {
        this.showFunctionDetail(params['functionId']);
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
    this.currentDiagramGroups[0] = { diagrams: trustDiagrams };
  }

  showFunctionDetail(functionId) {
    if (this.currentFunctionId) {
      this.selectedMethodSignatures = [];
      this.isLeaveAnimationEnabled = false;
      this.chRef.detectChanges();
      this.currentDiagramGroups = [];
      this.isLeaveAnimationEnabled = true;
    }
    this.currentFunctionId = functionId;
    this.isDiagramLoading = true;
    this.functionModelService.getDetail(functionId, (detail) => {
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
