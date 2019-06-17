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
import * as Stomp from 'webstomp-client';
import MenuItem from '@/domains/designdoc/MenuItem';
import SitCvWebsocket from '@/infrastructures/SitCvWebsocket';
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class DocTree extends Vue {
  public menuItems: MenuItem[] = [];

  public created() {
    this.drawMenu();
  }

  public async drawMenu() {
    const menuItems: MenuItem[] = await this.fetchMenuItems();
    this.menuItems = this.flattenMenuItems(menuItems, []);
  }

  public async fetchMenuItems(): Promise<MenuItem[]> {
    return new Promise((resolve) => {
      SitCvWebsocket.subscribe((client: Stomp.Client) => {
        client.subscribe('/topic/designdoc/list', (response: any) => {
          resolve(JSON.parse(response.body));
        });
        client.send('/app/designdoc/list');
      });
    });
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
