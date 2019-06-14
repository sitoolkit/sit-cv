import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/designdoc',
      name: 'designdoc',
      component: () => import('./views/Home.vue'),
      children: [
        {
          path: 'function/:functionId',
          name: 'function',
          component: () => import('./components/function/FunctionModel.vue'),
        },
      ],
    },
    {
      path: '*',
      redirect: 'designdoc',
    },
  ],
});
