<template>
  <v-treeview
    v-model="selection"
    :items="menuItems"
    dense
    open-on-click
    :open.sync="openNodes"
    item-key="id"
  >
    <template v-slot:label="{item, open}">
      <router-link :to="item.endpoint" v-if="item.endpoint">{{ item.name }}</router-link>
      <span v-else :data-item-id="item.id" :data-node-open="open" class="nodeLabel">{{ item.name }}</span>
    </template>
  </v-treeview>
</template>

<script lang="ts">
import MenuItem, { MenuItemUtils } from '@/domains/designdoc/MenuItem';
import { Component, Vue } from 'vue-property-decorator';
import DesignDocService from '@/domains/designdoc/DesignDocService';
import DesignDocServiceFactory from '@/domains/designdoc/DesignDocServiceFactory';
import DomUtils from '../../infrastructures/DomUtils';

@Component
export default class DocTree extends Vue {
  private designDocService: DesignDocService = DesignDocServiceFactory.getService();

  menuItems: MenuItem[] = [];

  openNodes: string[] = [];

  selection = [];

  created() {
    this.designDocService.fetchMenuItems((menuItems) => (this.menuItems = menuItems));
  }

  updated() {
    this.addTreeviewRecursiveOpenFunctionality();
  }

  /**
   * 1. Add click event handler to treeview node and node icon.
   * 2. Detect clicked node item key (= MenuItem.id).
   * 3. Open the node and its's descendants which have only one child.
   */
  addTreeviewRecursiveOpenFunctionality() {
    const _this = this;
    document
      .querySelectorAll('.v-treeview-node__root, .v-treeview-node__toggle')
      .forEach((treeNodeOrIcon) => {
        DomUtils.addEventListenerOnce(treeNodeOrIcon, 'click', this.onTreeviewNodeClick);
      });
  }

  onTreeviewNodeClick(event: Event) {
    if (!event.target || !(event.target instanceof HTMLElement)) {
      return;
    }

    const clickedNode = event.target;
    let targetElement;

    switch (clickedNode.tagName) {
      case 'I':
        targetElement = clickedNode.parentElement!;
        break;
      case 'DIV':
        targetElement = clickedNode;
      default:
        return;
    }

    const nodeLabel = <HTMLElement>targetElement.querySelector('.nodeLabel')!;
    if (nodeLabel.dataset.nodeOpen) {
      this.openNodeRecursively(nodeLabel.dataset.itemId!);
    }
  }

  openNodeRecursively(openItemId: string) {
    this.menuItems.forEach((menuItem) => {
      const foundMenuItem = MenuItemUtils.findByIdRecursively(openItemId, menuItem);
      if (foundMenuItem) {
        const foundIds = MenuItemUtils.collectIdsHavingSingleChild(foundMenuItem);
        foundIds
          .filter((foundId) => this.openNodes.indexOf(foundId) === -1)
          .forEach((foundId) => this.openNodes.push(foundId));
      }
    });
  }
}
</script>
