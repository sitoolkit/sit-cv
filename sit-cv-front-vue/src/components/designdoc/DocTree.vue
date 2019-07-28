<template>
  <v-treeview :items="menuItems">
    <template slot="label" slot-scope="props">
      <router-link :to="props.item.endpoint" v-if="props.item.endpoint">{{ props.item.name }}</router-link>
      <span v-else>{{ props.item.name }}</span>
    </template>
  </v-treeview>
</template>

<script lang="ts">
import MenuItem from '@/domains/designdoc/MenuItem';
import { Component, Vue } from 'vue-property-decorator';
import DesignDocService from '../../domains/designdoc/DesignDocService';
import DesignDocServerService from '../../domains/designdoc/DesignDocServerService';

@Component
export default class DocTree extends Vue {
  private designDocService: DesignDocService = DesignDocServerService;
  menuItems: object = [];

  public created() {
    this.drawMenu();
  }

  public async drawMenu() {
    this.menuItems = await this.designDocService.fetchMenuItems();
  }
}
</script>
