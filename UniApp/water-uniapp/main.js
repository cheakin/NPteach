import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import store from './store/index'
import uView from '@/uni_modules/uview-ui'
Vue.use(uView)
import myTabbar from './components/my-tabbar.vue'
Vue.component('my-tabbar', myTabbar)
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
    ...App,
		store
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import tabBar from './util/tabBar'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app,
		store
  }
}
// #endif