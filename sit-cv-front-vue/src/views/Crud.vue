<template>
  <div>
    <v-data-table
      :headers="headers"
      :items="rows"
      class="elevation-1"
      :items-per-page="10"
      show-group-by
      group-by="package"
    >
    </v-data-table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import DesignDocService from '@/domains/designdoc/DesignDocService';
import DesignDocServiceFactory from '@/domains/designdoc/DesignDocServiceFactory';

@Component
export default class Crud extends Vue {
  designDocService: DesignDocService = DesignDocServiceFactory.getService();

  headers: {}[] = [];

  rows: {}[] = [];

  created() {
    this.designDocService.getCrudModel().then((crudData) => {
      this.rows = crudData.rows;
      this.headers = crudData.headers.map((header) => {
        return { text: header, value: header };
      });
    });
  }
}
</script>
