<template>
  <div id="app">
    <keep-alive include="Home,DocTree">
      <router-view/>
    </keep-alive>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class Home extends Vue {}
</script>

<style lang="less">
</style>
