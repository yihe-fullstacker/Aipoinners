//入口文件
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import explain from './directives/explain.js'
import './assets/variables.css'

const app = createApp(App)
app.directive('explain', explain)
app.use(store).use(router).mount('#app')
