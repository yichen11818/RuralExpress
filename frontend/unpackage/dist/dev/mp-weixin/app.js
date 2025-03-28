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
  "./pages/user/verify.js";
  "./pages/courier/apply.js";
  "./pages/order/list.js";
  "./pages/about/about.js";
}
const _sfc_main = {
  globalData: {
    isLoggedIn: false,
    userInfo: null
  },
  onLaunch: function() {
    console.log("App Launch");
    this.checkLoginStatus();
  },
  onShow: function() {
    console.log("App Show");
  },
  onHide: function() {
    console.log("App Hide");
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      try {
        const token = common_vendor.index.getStorageSync("token");
        const userInfo = common_vendor.index.getStorageSync("userInfo");
        if (token && userInfo) {
          this.globalData.isLoggedIn = true;
          this.globalData.userInfo = typeof userInfo === "string" ? JSON.parse(userInfo) : userInfo;
          console.log("已登录", this.globalData.userInfo);
        } else {
          this.globalData.isLoggedIn = false;
          this.globalData.userInfo = null;
          console.log("未登录");
        }
      } catch (e) {
        console.error("检查登录状态失败", e);
        this.globalData.isLoggedIn = false;
        this.globalData.userInfo = null;
      }
    }
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
