"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/login/login.js";
  "./pages/user/user.js";
  "./pages/order/order.js";
  "./pages/user/profile-setup.js";
  "./pages/agreement/privacy.js";
  "./pages/agreement/user.js";
  "./pages/notice/detail.js";
  "./pages/delivery/send.js";
  "./pages/delivery/receive.js";
  "./pages/order/track.js";
  "./pages/courier/recruitment.js";
  "./pages/courier/list.js";
  "./pages/courier/detail.js";
  "./pages/user/settings.js";
  "./pages/user/profile.js";
  "./pages/user/address.js";
  "./pages/about/about.js";
}
const _sfc_main = {
  onLaunch: function() {
    console.log("App Launch");
  },
  onShow: function() {
    console.log("App Show");
  },
  onHide: function() {
    console.log("App Hide");
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  const pinia = common_vendor.createPinia();
  app.use(pinia);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
