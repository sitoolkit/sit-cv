import Vue from 'vue';
import App from './App.vue';
import router from './router';

Vue.config.productionTip = false;

Vue.config.errorHandler = (err, vm, info) => {

  // This handling is for the strange TypeError only happens the first navigation to crud.
  // We don't know the solution yet, so just reload it.
  if (
    err instanceof TypeError &&
    err.message.startsWith("Cannot create property 'isRootInsert' on string") &&
    location.href.endsWith('crud')
  ) {
    location.reload();

  } else {
    console.error(err);
  }
};

// Vuetify
import vuetify from './plugins/vuetify';

new Vue({
  router,
  vuetify,
  render: (h) => h(App),
}).$mount('#app');
