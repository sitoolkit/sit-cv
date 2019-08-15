<template>
  <div>
    <v-progress-linear indeterminate color="primary" :active="loading"></v-progress-linear>

      <div
        v-for="(diagram, diagramId) in (functionModelDetail ? functionModelDetail.diagrams : [])"
        class="diagram"
        v-html="diagram"
      ></div>

    <template v-if="functionModelApiDoc">
      <v-snackbar v-model="snackbar" :vertical="true">
        <div class="subtitle-1">{{functionModelApiDoc.qualifiedClassName}}</div>
        <div class="subtitle-1" v-for="annotation in functionModelApiDoc.annotations">{{annotation}}</div>
        <div class="title">{{functionModelApiDoc.methodDeclaration}}</div>
        <div v-for="content in functionModelApiDoc.contents" v-html="content"></div>

        <v-btn outlined @click="snackbar = false">Close</v-btn>
      </v-snackbar>
    </template>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import DesignDocService from '@/domains/designdoc/DesignDocService';
import FunctionModelDetail from '@/domains/designdoc/FunctionModelDetail';
import FunctionModelApiDoc from '@/domains/designdoc/FunctionModelApiDoc';
import DesignDocServiceFactory from '@/domains/designdoc/DesignDocServiceFactory';
import DomUtils from '@/infrastructures/DomUtils';

@Component
export default class FunctionModel extends Vue {
  designDocService: DesignDocService = DesignDocServiceFactory.getService();

  functionModelDetail: FunctionModelDetail | null = null;

  functionModelApiDoc: FunctionModelApiDoc | null = null;

  loading = false;

  snackbar = false;

  currentMethodSignature = '';

  mounted() {
    this.showFunctionDetail();
  }

  @Watch('$route')
  showFunctionDetail() {
    this.loading = true;
    this.designDocService.fetchFunctionModelDetail(
      this.$route.params.functionId,
      (functionModelDetail) => {
        this.loading = false;
        this.functionModelDetail = functionModelDetail;
      }
    );
  }

  updated() {
    const _this = this;

    document.querySelectorAll('.diagram a').forEach((a) => {
      const methodSignature = a.getAttribute('xlink:title')!;

      DomUtils.addEventListenerOnce(a, 'click', (event) => {
        _this.functionModelApiDoc = _this.functionModelDetail!.apiDocs[methodSignature];
        if (methodSignature === _this.currentMethodSignature) {
          _this.snackbar = !_this.snackbar;
        } else {
          _this.snackbar = true;
        }
        _this.currentMethodSignature = methodSignature;
      });
    });
  }
}
</script>

<style lang="css">
.diagram {
  overflow-x: auto;
}
</style>
