<template>
  <div>
    <vk-sticky>
      <vk-navbar>
        <vk-navbar-logo>Code Visualizer</vk-navbar-logo>
        <vk-navbar-item>
          <slot name="title"></slot>
        </vk-navbar-item>
      </vk-navbar>
    </vk-sticky>

    <div class="uk-flex">
      <doc-tree></doc-tree>
      <main>
        <slot></slot>
      </main>
    </div>
  </div>
</template>

<script lang="ts">
import DocTree from './DocTree.vue';
import { Component, Prop, Vue } from 'vue-property-decorator';

@Component({ components: { DocTree } })
export default class BaseFrame extends Vue {}
</script>
