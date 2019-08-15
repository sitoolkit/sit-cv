<template>
  <v-app>
    <v-navigation-drawer v-model="drawer" app temporary width="400">
      <doc-tree />
    </v-navigation-drawer>

    <v-app-bar app>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>

      <v-toolbar-title>Code Visualizer</v-toolbar-title>
    </v-app-bar>

    <v-content>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </v-content>

  </v-app>
</template>

<script lang="ts">
import DocTree from '@/components/designdoc/DocTree.vue';
import { Component, Vue } from 'vue-property-decorator';

@Component({ components: { DocTree } })
export default class App extends Vue {
  drawer = null;
}
</script>


<style lang="less">
</style>