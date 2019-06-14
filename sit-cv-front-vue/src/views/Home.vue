<template>
  <base-frame>
    <router-view></router-view>
  </base-frame>
</template>

<script lang="ts">
import BaseFrame from '@/components/designdoc/BaseFrame.vue';
import { Component, Vue } from 'vue-property-decorator';

@Component({ components: { BaseFrame } })
export default class Home extends Vue {}
</script>
