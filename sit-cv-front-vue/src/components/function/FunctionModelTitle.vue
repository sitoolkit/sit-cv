<template>
  <div>{{$route.params.functionId}}</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class FunctionModelTitle extends Vue {}
</script>
