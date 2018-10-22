import { Component, Inject, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import {MatSnackBar} from '@angular/material';
import * as $ from 'jquery';
import { DesignDocService } from '../../srv/designdoc/designdoc.service';
import { CommentComponent } from './comment/comment.component';

@Component({
  selector: 'app-designdoc',
  templateUrl: './designdoc.component.html',
  styleUrls: ['./designdoc.component.css'],
})

export class DesignDocComponent implements OnInit {
  designDocIds = [];
  currentDesignDocId = '';
  currentDiagrams = {};
  objectKeys = Object.keys;
  diagramComments = {};
  selectedMethodSignatures = [];
  currentMethodSignature = "";
  isDiagramLoading = false;

  constructor(
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    public snackBar: MatSnackBar,
    @Inject('DesignDocService') private ddService: DesignDocService) {}

  ngOnInit() {
      this.route.params.subscribe(params => {
        if (params['designDocId']) {
            this.showDesignDocDetail(params['designDocId']);
        }
      });
    }

  renderDiagrams(diagrams: object) {
    let trustDiagrams = {};
    Object.keys(diagrams).forEach((key) => {
      trustDiagrams[key] = this.sanitizer.bypassSecurityTrustHtml(diagrams[key]);
    });
    this.currentDiagrams = trustDiagrams;
  }

  showDesignDocDetail(designDocId) {
    if (this.currentDesignDocId) {
      this.currentDiagrams = {};
      this.selectedMethodSignatures = [];
    }
    this.currentDesignDocId = designDocId;
    this.isDiagramLoading = true;
    this.ddService.getDetail(designDocId, (detail) => {
      this.isDiagramLoading = false;
      this.renderDiagrams(detail.diagrams);
      this.diagramComments = detail.comments;
    })
    return false;
  }

  methodNameClick(event) {
    let link: JQuery = $(event.target).closest('a');
    if (link.length > 0) {
      this.snackBar.openFromComponent(CommentComponent, {
            data: {
                commentId: link.attr('xlink:title'),
                comments: this.diagramComments
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
