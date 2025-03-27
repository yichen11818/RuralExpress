"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      addressList: [
        {
          id: 1,
          name: "张三",
          phone: "13800138000",
          province: "广东省",
          city: "深圳市",
          district: "南山区",
          address: "科技园路1号",
          isDefault: true
        },
        {
          id: 2,
          name: "李四",
          phone: "13900139000",
          province: "广东省",
          city: "深圳市",
          district: "福田区",
          address: "中心城区1号",
          isDefault: false
        }
      ],
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
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone)
        return "";
      return phone.substring(0, 3) + "****" + phone.substring(7);
    },
    // 编辑地址
    editAddress(item) {
      common_vendor.index.showToast({
        title: "编辑地址功能开发中",
        icon: "none"
      });
    },
    // 删除地址
    deleteAddress(id) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除该地址吗？",
        success: (res) => {
          if (res.confirm) {
            this.addressList = this.addressList.filter((item) => item.id !== id);
            common_vendor.index.showToast({
              title: "删除成功",
              icon: "success"
            });
          }
        }
      });
    },
    // 选择地址
    selectAddress(item) {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      if (currentPage.options && currentPage.options.type === "select") {
        const prevPage = pages[pages.length - 2];
        if (prevPage && prevPage.$vm) {
          prevPage.$vm.selectedAddress = item;
        }
        common_vendor.index.navigateBack();
      }
    },
    // 添加新地址
    addAddress() {
      common_vendor.index.showToast({
        title: "添加地址功能开发中",
        icon: "none"
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
    a: $data.statusBarHeight + "px",
    b: common_vendor.p({
      type: "left",
      size: "18",
      color: "#333"
    }),
    c: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    d: $data.addressList.length > 0
  }, $data.addressList.length > 0 ? {
    e: common_vendor.f($data.addressList, (item, index, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.name),
        b: common_vendor.t($options.formatPhone(item.phone)),
        c: item.isDefault
      }, item.isDefault ? {} : {}, {
        d: common_vendor.t(item.province + item.city + item.district + item.address),
        e: common_vendor.o(($event) => $options.selectAddress(item), index),
        f: "7efb1000-1-" + i0,
        g: common_vendor.o(($event) => $options.editAddress(item), index),
        h: "7efb1000-2-" + i0,
        i: common_vendor.o(($event) => $options.deleteAddress(item.id), index),
        j: index
      });
    }),
    f: common_vendor.p({
      type: "compose",
      size: "16",
      color: "#666"
    }),
    g: common_vendor.p({
      type: "trash",
      size: "16",
      color: "#666"
    })
  } : {
    h: common_assets._imports_0$2
  }, {
    i: common_vendor.o((...args) => $options.addAddress && $options.addAddress(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
