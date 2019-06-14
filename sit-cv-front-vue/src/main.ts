import Vue from 'vue';
import App from './App.vue';
import router from './router';

Vue.config.productionTip = false;

// Vuikit
import Vuikit from 'vuikit';
import VuikitIcons from '@vuikit/icons';

import './assets/uikit.mono-am-theme.min.css';
import './assets/uikit.js';

Vue.use(Vuikit);
Vue.use(VuikitIcons);

new Vue({
  router,
  render: (h) => h(App),
}).$mount('#app');
