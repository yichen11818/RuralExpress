"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const _sfc_main = {
  data() {
    return {
      notifications: true,
      locationEnabled: true,
      statusBarHeight: 20
      // 默认值
    };
  },
  onLoad() {
    this.getStatusBarHeight();
  },
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      common_vendor.index.getSystemInfo({
        success: (res) => {
          this.statusBarHeight = res.statusBarHeight || 20;
        }
      });
    },
    goBack() {
      common_vendor.index.navigateBack();
    },
    navigateTo(url) {
      common_vendor.index.navigateTo({
        url
      });
    },
    changePassword() {
      common_vendor.index.showToast({
        title: "功能开发中",
        icon: "none"
      });
    },
    toggleNotifications(e) {
      this.notifications = e.detail.value;
      common_vendor.index.showToast({
        title: this.notifications ? "已开启推送通知" : "已关闭推送通知",
        icon: "none"
      });
    },
    toggleLocation(e) {
      this.locationEnabled = e.detail.value;
      common_vendor.index.showToast({
        title: this.locationEnabled ? "已开启定位服务" : "已关闭定位服务",
        icon: "none"
      });
    },
    clearCache() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要清除缓存吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.clearStorageSync();
            common_vendor.index.showToast({
              title: "缓存已清除",
              icon: "success"
            });
          }
        }
      });
    },
    handleLogout() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            api_auth.logout();
            common_vendor.index.reLaunch({
              url: "/pages/login/login"
            });
          }
        }
      });
    }
  }
};
if (!Array) {
  const _easycom_uni_icons2 = common_vendor.resolveComponent("uni-icons");
  _easycom_uni_icons2();
}
const _easycom_uni_icons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
if (!Math) {
  _easycom_uni_icons();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $data.statusBarHeight + "px",
    b: common_vendor.p({
      type: "left",
      size: "18",
      color: "#333"
    }),
    c: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    d: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    e: common_vendor.o(($event) => $options.navigateTo("/pages/user/profile")),
    f: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    g: common_vendor.o((...args) => $options.changePassword && $options.changePassword(...args)),
    h: $data.notifications,
    i: common_vendor.o((...args) => $options.toggleNotifications && $options.toggleNotifications(...args)),
    j: $data.locationEnabled,
    k: common_vendor.o((...args) => $options.toggleLocation && $options.toggleLocation(...args)),
    l: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    m: common_vendor.o((...args) => $options.clearCache && $options.clearCache(...args)),
    n: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    o: common_vendor.o(($event) => $options.navigateTo("/pages/about/about")),
    p: common_vendor.o((...args) => $options.handleLogout && $options.handleLogout(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
