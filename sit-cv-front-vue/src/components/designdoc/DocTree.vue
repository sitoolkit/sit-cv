<template>
  <div>
    <ul>
      <li v-for="item in menuItems" v-bind:key="item.endpoint">
        <router-link :to="item.endpoint">{{item.name}}</router-link>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import MenuItem from '@/domains/designdoc/MenuItem';
import { Component, Vue } from 'vue-property-decorator';
import DesignDocService from '../../domains/designdoc/DesignDocService';
import DesignDocServerService from '../../domains/designdoc/DesignDocServerService';

@Component
export default class DocTree extends Vue {
  private designDocService: DesignDocService = DesignDocServerService;

  private menuItems: MenuItem[] = [];

  public created() {
    this.drawMenu();
  }

  public async drawMenu() {
    const menuItems: MenuItem[] = await this.designDocService.fetchMenuItems();
    this.menuItems = this.flattenMenuItems(menuItems, []);
  }

  private flattenMenuItems(menuItems: MenuItem[], result: MenuItem[]): MenuItem[] {
    menuItems.forEach((item) => {
      if (item.endpoint) {
        result.push(item);
      }
      if (item.children) {
        this.flattenMenuItems(item.children, result);
      }
    });
    return result;
  }
}
</script>
