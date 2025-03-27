"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const _sfc_main = {
  data() {
    return {
      userInfo: {}
    };
  },
  onShow() {
    if (!api_auth.isLoggedIn()) {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
      return;
    }
    this.userInfo = api_auth.getUserInfo() || {};
  },
  methods: {
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone)
        return "";
      return phone.substring(0, 3) + "****" + phone.substring(7);
    },
    // 页面导航
    navigateTo(url) {
      common_vendor.index.navigateTo({
        url
      });
    },
    // 退出登录
    handleLogout() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            api_auth.logout();
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
  return common_vendor.e({
    a: $data.userInfo.avatar || "/static/images/default-avatar.png",
    b: common_vendor.t($data.userInfo.nickname || "游客"),
    c: common_vendor.t($options.formatPhone($data.userInfo.phone)),
    d: $data.userInfo.verified
  }, $data.userInfo.verified ? {} : {}, {
    e: $data.userInfo.userType === 1
  }, $data.userInfo.userType === 1 ? {} : {}, {
    f: common_vendor.p({
      type: "gear",
      size: "24",
      color: "#333"
    }),
    g: common_vendor.o(($event) => $options.navigateTo("/pages/user/settings")),
    h: common_vendor.p({
      type: "person",
      size: "20",
      color: "#3cc51f"
    }),
    i: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    j: common_vendor.o(($event) => $options.navigateTo("/pages/user/profile")),
    k: common_vendor.p({
      type: "location",
      size: "20",
      color: "#3cc51f"
    }),
    l: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    m: common_vendor.o(($event) => $options.navigateTo("/pages/user/address")),
    n: !$data.userInfo.verified
  }, !$data.userInfo.verified ? {
    o: common_vendor.p({
      type: "paperclip",
      size: "20",
      color: "#3cc51f"
    }),
    p: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    q: common_vendor.o(($event) => $options.navigateTo("/pages/user/verify"))
  } : {}, {
    r: $data.userInfo.userType !== 1
  }, $data.userInfo.userType !== 1 ? {
    s: common_vendor.p({
      type: "staff",
      size: "20",
      color: "#3cc51f"
    }),
    t: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    v: common_vendor.o(($event) => $options.navigateTo("/pages/courier/apply"))
  } : {}, {
    w: common_vendor.p({
      type: "list",
      size: "20",
      color: "#3cc51f"
    }),
    x: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    y: common_vendor.o(($event) => $options.navigateTo("/pages/order/list?status=all")),
    z: common_vendor.p({
      type: "waiting",
      size: "20",
      color: "#3cc51f"
    }),
    A: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    B: common_vendor.o(($event) => $options.navigateTo("/pages/order/list?status=pending")),
    C: common_vendor.p({
      type: "checkmarkempty",
      size: "20",
      color: "#3cc51f"
    }),
    D: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    E: common_vendor.o(($event) => $options.navigateTo("/pages/order/list?status=completed")),
    F: common_vendor.p({
      type: "help",
      size: "20",
      color: "#3cc51f"
    }),
    G: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    H: common_vendor.o(($event) => $options.navigateTo("/pages/help/help")),
    I: common_vendor.p({
      type: "chat",
      size: "20",
      color: "#3cc51f"
    }),
    J: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    K: common_vendor.o(($event) => $options.navigateTo("/pages/feedback/feedback")),
    L: common_vendor.p({
      type: "info",
      size: "20",
      color: "#3cc51f"
    }),
    M: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    N: common_vendor.o(($event) => $options.navigateTo("/pages/about/about")),
    O: common_vendor.o((...args) => $options.handleLogout && $options.handleLogout(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
