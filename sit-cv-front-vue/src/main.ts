import Vue from 'vue';
import App from './App.vue';
import router from './router';

Vue.config.productionTip = false;

// UIkit, Vuikit
import './assets/uikit.sit-cv-theme.min.css';
import './assets/uikit.min.js';

import Vuikit from 'vuikit';
import VuikitIcons from '@vuikit/icons';

Vue.use(Vuikit);
Vue.use(VuikitIcons);

new Vue({
  router,
  render: (h) => h(App),
}).$mount('#app');
