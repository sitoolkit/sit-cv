<template>
  <v-simple-table dense>
    <template v-slot:default>
      <thead>
        <tr>
          <th>EntryPoint</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="entryPoint in entryPoints">
          <td>
            <router-link :to="entryPoint.url">{{ entryPoint.id }}</router-link>
          </td>
        </tr>
      </tbody>
    </template>
  </v-simple-table>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import DesignDocService from '@/domains/designdoc/DesignDocService';
import DesignDocServiceFactory from '@/domains/designdoc/DesignDocServiceFactory';
import EntryPoint from '@/domains/designdoc/EntryPoint';
import MenuItem from '@/domains/designdoc/MenuItem';

@Component
export default class Home extends Vue {
  designDocService: DesignDocService = DesignDocServiceFactory.getService();

  entryPoints: EntryPoint[] = [];

  created() {
    this.designDocService.fetchMenuItems((menuItems) => (this.menuItemToEntryPointRecursively(menuItems)));
  }

  menuItemToEntryPointRecursively(menuItems: MenuItem[]) {
    menuItems.forEach((menuItem) => {
      if (menuItem.id == "crud-matrix") {
        return;

      } else if (menuItem.children.length > 0) {
        this.menuItemToEntryPointRecursively(menuItem.children);

      } else if (menuItem.endpoint != null) {
        var entryPointId = menuItem.id.substr(0, menuItem.id.lastIndexOf("."));
        this.entryPoints.push(new EntryPoint(entryPointId, menuItem.endpoint));
      }
    });
  }
}
</script>
