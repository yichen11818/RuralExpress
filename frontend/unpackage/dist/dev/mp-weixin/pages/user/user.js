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
    console.log("当前用户信息:", this.userInfo);
    console.log("用户类型 userType:", this.userInfo.userType);
    console.log("管理员入口条件检查:", this.userInfo.userType === 2);
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
      console.log("导航到:", url);
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
    f: $data.userInfo.userType === 2
  }, $data.userInfo.userType === 2 ? {} : {}, {
    g: common_vendor.p({
      type: "gear",
      size: "24",
      color: "#333"
    }),
    h: common_vendor.o(($event) => $options.navigateTo("/pages/user/settings")),
    i: $data.userInfo.userType === 2
  }, $data.userInfo.userType === 2 ? {
    j: common_vendor.p({
      type: "person-filled",
      size: "20",
      color: "#ff7043"
    }),
    k: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    l: common_vendor.o(($event) => $options.navigateTo("/pages/admin/users/index")),
    m: common_vendor.p({
      type: "staff",
      size: "20",
      color: "#ff7043"
    }),
    n: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    o: common_vendor.o(($event) => $options.navigateTo("/pages/admin/couriers/index")),
    p: common_vendor.p({
      type: "shop",
      size: "20",
      color: "#ff7043"
    }),
    q: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    r: common_vendor.o(($event) => $options.navigateTo("/pages/admin/stations/index")),
    s: common_vendor.p({
      type: "list",
      size: "20",
      color: "#ff7043"
    }),
    t: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    v: common_vendor.o(($event) => $options.navigateTo("/pages/admin/orders/index")),
    w: common_vendor.p({
      type: "cloud",
      size: "20",
      color: "#ff7043"
    }),
    x: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    y: common_vendor.o(($event) => $options.navigateTo("/pages/admin/companies/index")),
    z: common_vendor.p({
      type: "gear-filled",
      size: "20",
      color: "#ff7043"
    }),
    A: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    B: common_vendor.o(($event) => $options.navigateTo("/pages/admin/system/index"))
  } : {}, {
    C: common_vendor.p({
      type: "person",
      size: "20",
      color: "#3cc51f"
    }),
    D: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    E: common_vendor.o(($event) => $options.navigateTo("/pages/user/profile")),
    F: common_vendor.p({
      type: "location",
      size: "20",
      color: "#3cc51f"
    }),
    G: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    H: common_vendor.o(($event) => $options.navigateTo("/pages/user/address")),
    I: !$data.userInfo.verified
  }, !$data.userInfo.verified ? {
    J: common_vendor.p({
      type: "paperclip",
      size: "20",
      color: "#3cc51f"
    }),
    K: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    L: common_vendor.o(($event) => $options.navigateTo("/pages/user/verify"))
  } : {}, {
    M: $data.userInfo.userType !== 1 && $data.userInfo.userType !== 2
  }, $data.userInfo.userType !== 1 && $data.userInfo.userType !== 2 ? {
    N: common_vendor.p({
      type: "staff",
      size: "20",
      color: "#3cc51f"
    }),
    O: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    P: common_vendor.o(($event) => $options.navigateTo("/pages/courier/apply"))
  } : {}, {
    Q: common_vendor.p({
      type: "list",
      size: "20",
      color: "#3cc51f"
    }),
    R: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    S: common_vendor.o(($event) => $options.navigateTo("/pages/order/list?status=all")),
    T: common_vendor.p({
      type: "waiting",
      size: "20",
      color: "#3cc51f"
    }),
    U: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    V: common_vendor.o(($event) => $options.navigateTo("/pages/order/list?status=pending")),
    W: common_vendor.p({
      type: "checkmarkempty",
      size: "20",
      color: "#3cc51f"
    }),
    X: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    Y: common_vendor.o(($event) => $options.navigateTo("/pages/order/list?status=completed")),
    Z: common_vendor.p({
      type: "help",
      size: "20",
      color: "#3cc51f"
    }),
    aa: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    ab: common_vendor.o(($event) => $options.navigateTo("/pages/help/help")),
    ac: common_vendor.p({
      type: "chat",
      size: "20",
      color: "#3cc51f"
    }),
    ad: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    ae: common_vendor.o(($event) => $options.navigateTo("/pages/feedback/feedback")),
    af: common_vendor.p({
      type: "info",
      size: "20",
      color: "#3cc51f"
    }),
    ag: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    ah: common_vendor.o(($event) => $options.navigateTo("/pages/about/about")),
    ai: common_vendor.o((...args) => $options.handleLogout && $options.handleLogout(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
