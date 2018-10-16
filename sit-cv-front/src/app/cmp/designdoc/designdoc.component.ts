import { Component, Inject} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import * as $ from 'jquery';
import { DesignDocService } from '../../srv/designdoc/designdoc.service';

@Component({
  selector: 'app-designdoc',
  templateUrl: './designdoc.component.html',
  styleUrls: ['./designdoc.component.css'],
})

export class DesignDocComponent {
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
    @Inject('DesignDocService') private ddService: DesignDocService) {}

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
      this.toggleComment(link);
    }
    return false;
  }

  toggleComment(link: JQuery) {
    let title: string = link.attr('xlink:title');
    let index: number = this.selectedMethodSignatures.indexOf(title);
    if (index < 0) {
      this.selectedMethodSignatures.push(title);
    } else {
      this.selectedMethodSignatures.splice(index, 1);
    }
  }

  methodNameMouseover(event) {
    let link: JQuery = $(event.target).closest('a');
    this.currentMethodSignature = link.attr('xlink:title');
    return false;
  }
}
