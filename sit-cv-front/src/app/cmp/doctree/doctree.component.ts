import { Component, Inject } from '@angular/core';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { DesignDocService } from '../../srv/designdoc/designdoc.service';
import { DesignDocMenuItem } from 'src/app/srv/designdoc/designdoc-menu-item';

@Component({
  selector: 'app-doctree',
  templateUrl: './doctree.component.html',
  styleUrls: ['./doctree.component.css']
})
export class DoctreeComponent {

  nestedTreeControl = new NestedTreeControl<DesignDocMenuItem>((item: DesignDocMenuItem) => item.children);
  nestedDataSource = new MatTreeNestedDataSource();

  constructor( @Inject('DesignDocService') private ddService: DesignDocService) {
    this.ddService.getMenuList((menuItems) => {
      this.nestedDataSource.data = menuItems;
    });
  }

  hasNestedChild = (_: number, item: DesignDocMenuItem) => !item.endpoint;

  toggleExpanded(item: DesignDocMenuItem) {
    if (this.nestedTreeControl.isExpanded(item)) {
      this.nestedTreeControl.collapse(item);
    } else {
      this.expandRecursively(item);
    }
  }

  expandRecursively(item: DesignDocMenuItem) {
    this.nestedTreeControl.expand(item);
    if (item.children.length === 1) {
      this.expandRecursively(item.children[0]);
    }
  }

}
