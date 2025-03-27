import App from './App.vue';
import { createSSRApp } from 'vue';
import { createPinia } from 'pinia';

// 不再直接导入uni-icons组件
// 推荐使用easycom模式，在pages.json中配置

export function createApp() {
  const app = createSSRApp(App);
  const pinia = createPinia();
  
  app.use(pinia);
  
  // 移除全局组件注册
  
  return {
    app
  };
} 