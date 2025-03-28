"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
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
    d: common_assets._imports_0$6,
    e: common_vendor.p({
      type: "phone",
      size: "18",
      color: "#3cc51f"
    }),
    f: common_vendor.p({
      type: "email",
      size: "18",
      color: "#3cc51f"
    }),
    g: common_vendor.p({
      type: "location",
      size: "18",
      color: "#3cc51f"
    })
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
