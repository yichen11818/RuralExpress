"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      activeTab: "login",
      // 当前激活的标签页
      // 登录表单
      loginForm: {
        phone: "",
        password: "",
        rememberMe: false,
        agreement: false
      },
      // 注册表单
      registerForm: {
        phone: "",
        password: "",
        agreement: false
      }
    };
  },
  methods: {
    // 切换标签页
    switchTab(tab) {
      this.activeTab = tab;
    },
    // 处理登录
    async handleLogin() {
      if (!this.loginForm.phone) {
        common_vendor.index.showToast({
          title: "请输入手机号",
          icon: "none"
        });
        return;
      }
      if (!this.loginForm.password) {
        common_vendor.index.showToast({
          title: "请输入密码",
          icon: "none"
        });
        return;
      }
      if (!this.loginForm.agreement) {
        common_vendor.index.showToast({
          title: "请阅读并同意相关协议",
          icon: "none"
        });
        return;
      }
      try {
        common_vendor.index.showLoading({
          title: "登录中...",
          mask: true
        });
        const res = await api_auth.login(this.loginForm);
        api_auth.saveLoginInfo(res.data);
        common_vendor.index.hideLoading();
        if (!res.data.user.nickname || !res.data.user.avatar) {
          const userInfoParam = encodeURIComponent(JSON.stringify(res.data.user));
          common_vendor.index.navigateTo({
            url: `../user/profile-setup?userInfo=${userInfoParam}`
          });
        } else {
          common_vendor.index.switchTab({
            url: "/pages/index/index"
          });
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        console.error("登录失败", error);
      }
    },
    // 处理注册
    async handleRegister() {
      if (!this.registerForm.phone) {
        common_vendor.index.showToast({
          title: "请输入手机号",
          icon: "none"
        });
        return;
      }
      if (!this.registerForm.password) {
        common_vendor.index.showToast({
          title: "请输入密码",
          icon: "none"
        });
        return;
      }
      if (!this.registerForm.agreement) {
        common_vendor.index.showToast({
          title: "请阅读并同意相关协议",
          icon: "none"
        });
        return;
      }
      try {
        common_vendor.index.showLoading({
          title: "注册中...",
          mask: true
        });
        const res = await api_auth.register(this.registerForm);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "注册成功",
          icon: "success"
        });
        const loginRes = await api_auth.login({
          phone: this.registerForm.phone,
          password: this.registerForm.password
        });
        api_auth.saveLoginInfo(loginRes.data);
        const userInfoParam = encodeURIComponent(JSON.stringify(loginRes.data.user));
        common_vendor.index.navigateTo({
          url: `../user/profile-setup?userInfo=${userInfoParam}`
        });
      } catch (error) {
        common_vendor.index.hideLoading();
        console.error("注册失败", error);
      }
    },
    // 页面导航
    navigateTo(url) {
      common_vendor.index.navigateTo({
        url
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_assets._imports_0$1,
    b: $data.activeTab === "login" ? 1 : "",
    c: common_vendor.o(($event) => $options.switchTab("login")),
    d: $data.activeTab === "register" ? 1 : "",
    e: common_vendor.o(($event) => $options.switchTab("register")),
    f: $data.activeTab === "login"
  }, $data.activeTab === "login" ? {
    g: $data.loginForm.phone,
    h: common_vendor.o(($event) => $data.loginForm.phone = $event.detail.value),
    i: $data.loginForm.password,
    j: common_vendor.o(($event) => $data.loginForm.password = $event.detail.value),
    k: $data.loginForm.rememberMe,
    l: common_vendor.o(($event) => $data.loginForm.rememberMe = !$data.loginForm.rememberMe),
    m: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args)),
    n: $data.loginForm.agreement,
    o: common_vendor.o(($event) => $data.loginForm.agreement = !$data.loginForm.agreement),
    p: common_vendor.o(($event) => $options.navigateTo("../agreement/privacy")),
    q: common_vendor.o(($event) => $options.navigateTo("../agreement/user"))
  } : {}, {
    r: $data.activeTab === "register"
  }, $data.activeTab === "register" ? {
    s: $data.registerForm.phone,
    t: common_vendor.o(($event) => $data.registerForm.phone = $event.detail.value),
    v: $data.registerForm.password,
    w: common_vendor.o(($event) => $data.registerForm.password = $event.detail.value),
    x: common_vendor.o((...args) => $options.handleRegister && $options.handleRegister(...args)),
    y: $data.registerForm.agreement,
    z: common_vendor.o(($event) => $data.registerForm.agreement = !$data.registerForm.agreement),
    A: common_vendor.o(($event) => $options.navigateTo("../agreement/privacy")),
    B: common_vendor.o(($event) => $options.navigateTo("../agreement/user"))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
