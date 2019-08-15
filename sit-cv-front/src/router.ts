import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/designdoc',
      name: 'designdoc',
      component: () => import('@/views/Home.vue'),
    },
    {
      path: '/designdoc/data/crud',
      name: 'crud',
      component: () => import('@/views/Crud.vue'),
    },
    {
      path: '/designdoc/function/:functionId',
      name: 'function',
      component: () => import('@/views/FunctionModel.vue'),
    },
    {
      path: '*',
      redirect: 'designdoc',
    },
  ],
});
