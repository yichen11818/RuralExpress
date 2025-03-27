"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_auth = require("../../utils/auth.js");
const common_assets = require("../../common/assets.js");
const AddressForm = () => "../../components/AddressForm.js";
const _sfc_main = {
  components: {
    AddressForm
  },
  data() {
    return {
      addressList: [],
      statusBarHeight: 20,
      // 默认值
      formType: "add",
      // add 或 edit
      currentAddress: null,
      // 当前编辑的地址对象
      isLoading: false
      // 加载状态
    };
  },
  onLoad() {
    this.getStatusBarHeight();
    this.checkLogin();
  },
  onShow() {
    this.loadAddressList();
  },
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      try {
        const res = common_vendor.wx$1.getWindowInfo();
        this.statusBarHeight = res.statusBarHeight || 20;
      } catch (error) {
        try {
          const res = common_vendor.index.getSystemInfoSync();
          this.statusBarHeight = res.statusBarHeight || 20;
        } catch (e) {
          this.statusBarHeight = 20;
          console.error("获取状态栏高度失败", e);
        }
      }
    },
    goBack() {
      common_vendor.index.navigateBack();
    },
    // 加载地址列表
    async loadAddressList() {
      this.isLoading = true;
      try {
        setTimeout(() => {
          this.addressList = [
            {
              id: "1",
              name: "张三",
              phone: "13800138000",
              province: "广东省",
              city: "深圳市",
              district: "南山区",
              detailAddress: "科技园路1号",
              addressType: "公司",
              isDefault: true
            },
            {
              id: "2",
              name: "李四",
              phone: "13900139000",
              province: "广东省",
              city: "深圳市",
              district: "福田区",
              detailAddress: "中心城区1号",
              addressType: "家",
              isDefault: false
            }
          ];
          this.isLoading = false;
        }, 500);
      } catch (error) {
        console.error("加载地址列表失败", error);
        common_vendor.index.showToast({
          title: "加载地址列表失败",
          icon: "none"
        });
        this.isLoading = false;
      }
    },
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone)
        return "";
      return phone.substring(0, 3) + "****" + phone.substring(7);
    },
    // 显示地址表单弹窗
    showAddressForm(type, address = null) {
      this.formType = type;
      this.currentAddress = address;
      this.$refs.addressPopup.open();
    },
    // 隐藏地址表单弹窗
    hideAddressForm() {
      this.$refs.addressPopup.close();
    },
    // 编辑地址
    editAddress(item) {
      this.showAddressForm("edit", item);
    },
    // 处理地址表单提交
    async handleAddressSubmit(addressData) {
      try {
        if (this.formType === "add") {
          addressData.id = Date.now().toString();
          this.addressList.push(addressData);
          common_vendor.index.showToast({
            title: "添加成功",
            icon: "success"
          });
        } else {
          const index = this.addressList.findIndex((item) => item.id === addressData.id);
          if (index !== -1) {
            this.addressList[index] = addressData;
          }
          common_vendor.index.showToast({
            title: "更新成功",
            icon: "success"
          });
        }
        if (addressData.isDefault) {
          this.addressList.forEach((item) => {
            if (item.id !== addressData.id) {
              item.isDefault = false;
            }
          });
        }
        this.hideAddressForm();
      } catch (error) {
        console.error("提交地址失败", error);
        common_vendor.index.showToast({
          title: "提交失败，请重试",
          icon: "none"
        });
      }
    },
    // 删除地址
    deleteAddress(id) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除该地址吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              this.addressList = this.addressList.filter((item) => item.id !== id);
              common_vendor.index.showToast({
                title: "删除成功",
                icon: "success"
              });
            } catch (error) {
              console.error("删除地址失败", error);
              common_vendor.index.showToast({
                title: "删除失败，请重试",
                icon: "none"
              });
            }
          }
        }
      });
    },
    // 设置默认地址
    async setDefault(id) {
      try {
        this.addressList.forEach((item) => {
          item.isDefault = item.id === id;
        });
        common_vendor.index.showToast({
          title: "设置成功",
          icon: "success"
        });
      } catch (error) {
        console.error("设置默认地址失败", error);
        common_vendor.index.showToast({
          title: "设置失败，请重试",
          icon: "none"
        });
      }
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
    // 检查登录状态
    checkLogin() {
      utils_auth.checkLogin(true);
    }
  }
};
if (!Array) {
  const _easycom_uni_icons2 = common_vendor.resolveComponent("uni-icons");
  const _component_address_form = common_vendor.resolveComponent("address-form");
  const _easycom_uni_popup2 = common_vendor.resolveComponent("uni-popup");
  (_easycom_uni_icons2 + _component_address_form + _easycom_uni_popup2)();
}
const _easycom_uni_icons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const _easycom_uni_popup = () => "../../uni_modules/uni-popup/components/uni-popup/uni-popup.js";
if (!Math) {
  (_easycom_uni_icons + _easycom_uni_popup)();
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
        d: common_vendor.t(item.province + item.city + item.district + " " + item.detailAddress),
        e: common_vendor.t(item.addressType),
        f: common_vendor.o(($event) => $options.selectAddress(item), index),
        g: "7efb1000-1-" + i0,
        h: common_vendor.o(($event) => $options.editAddress(item), index),
        i: "7efb1000-2-" + i0,
        j: common_vendor.o(($event) => $options.deleteAddress(item.id), index),
        k: !item.isDefault
      }, !item.isDefault ? {
        l: "7efb1000-3-" + i0,
        m: common_vendor.p({
          type: "star",
          size: "16",
          color: "#666"
        }),
        n: common_vendor.o(($event) => $options.setDefault(item.id), index)
      } : {}, {
        o: index
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
    i: common_vendor.o(($event) => $options.showAddressForm("add")),
    j: common_vendor.t($data.formType === "add" ? "添加新地址" : "编辑地址"),
    k: common_vendor.p({
      type: "close",
      size: "20",
      color: "#333"
    }),
    l: common_vendor.o((...args) => $options.hideAddressForm && $options.hideAddressForm(...args)),
    m: common_vendor.o($options.handleAddressSubmit),
    n: common_vendor.p({
      type: $data.formType,
      address: $data.currentAddress
    }),
    o: common_vendor.sr("addressPopup", "7efb1000-4"),
    p: common_vendor.p({
      type: "bottom"
    })
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
