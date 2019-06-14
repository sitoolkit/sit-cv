<template>
  <div>Function Model {{$route.params.functionId}}</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class FunctionModel extends Vue {}
</script>
