import App from './App.vue';
import { createSSRApp } from 'vue';
import { createPinia } from 'pinia';
import config from './utils/config'; // 导入配置文件
import api from './api'; // 导入API模块

// 不再直接导入uni-icons组件
// 推荐使用easycom模式，在pages.json中配置

export function createApp() {
  const app = createSSRApp(App);
  const pinia = createPinia();
  
  app.use(pinia);
  
  // 将配置注册为全局属性
  app.config.globalProperties.$config = config;
  app.config.globalProperties.$api = api;
  
  return {
    app
  };
} 