"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const _sfc_main = {
  data() {
    return {
      // 表单数据
      formData: {
        // 寄件人信息
        senderName: "",
        senderPhone: "",
        senderAddress: "",
        // 收件人信息
        receiverName: "",
        receiverPhone: "",
        receiverAddress: "",
        // 包裹信息
        packageType: 0,
        // 默认普通快递
        weight: 1,
        // 默认1kg
        insuranceValue: 0,
        // 默认不保价
        note: "",
        // 服务选项
        isUrgent: false,
        // 是否加急
        needReceipt: false,
        // 是否需要回执
        // 支付方式
        paymentMethod: "wxpay"
        // 默认微信支付
      },
      // 包裹类型选项
      packageTypes: ["普通快递", "文件", "食品", "电子产品", "易碎品", "其他"],
      // 加急费用
      urgentFee: 10,
      // 支付方式
      paymentMethods: [
        { name: "微信支付", value: "wxpay", icon: "/static/images/wxpay.png" },
        { name: "支付宝", value: "alipay", icon: "/static/images/alipay.png" },
        { name: "余额支付", value: "balance", icon: "/static/images/balance.png" }
      ]
    };
  },
  onLoad(options) {
    if (!api_auth.isLoggedIn()) {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
      return;
    }
    const userInfo = api_auth.getUserInfo();
    if (userInfo) {
      this.formData.senderName = userInfo.name || userInfo.nickname || "";
      this.formData.senderPhone = userInfo.phone || "";
    }
    const eventChannel = this.getOpenerEventChannel();
    if (eventChannel && eventChannel.on) {
      eventChannel.on("reorderData", (data) => {
        if (data) {
          this.formData.senderName = data.senderName || this.formData.senderName;
          this.formData.senderPhone = data.senderPhone || this.formData.senderPhone;
          this.formData.senderAddress = data.senderAddress || this.formData.senderAddress;
          this.formData.receiverName = data.receiverName || this.formData.receiverName;
          this.formData.receiverPhone = data.receiverPhone || this.formData.receiverPhone;
          this.formData.receiverAddress = data.receiverAddress || this.formData.receiverAddress;
          this.formData.packageType = data.packageType !== void 0 ? data.packageType : this.formData.packageType;
          this.formData.weight = data.weight || this.formData.weight;
        }
      });
    }
  },
  methods: {
    // 显示地址簿
    showAddressBook(type) {
      common_vendor.index.navigateTo({
        url: "/pages/user/address",
        success: (res) => {
          res.eventChannel.once("selectAddress", (data) => {
            if (type === "sender") {
              this.formData.senderName = data.name;
              this.formData.senderPhone = data.phone;
              this.formData.senderAddress = data.address;
            } else {
              this.formData.receiverName = data.name;
              this.formData.receiverPhone = data.phone;
              this.formData.receiverAddress = data.address;
            }
          });
        }
      });
    },
    // 物品类型变更
    handlePackageTypeChange(e) {
      this.formData.packageType = parseInt(e.detail.value);
    },
    // 支付方式变更
    handlePaymentChange(e) {
      this.formData.paymentMethod = e.detail.value;
    },
    // 计算配送费
    calcDeliveryFee() {
      const weight = parseFloat(this.formData.weight) || 0;
      if (weight <= 1) {
        return 15;
      } else if (weight <= 5) {
        return 15 + (weight - 1) * 5;
      } else {
        return 35 + (weight - 5) * 4;
      }
    },
    // 计算保价费
    calcInsuranceFee() {
      const value = parseFloat(this.formData.insuranceValue) || 0;
      return Math.max(value * 0.02, value > 0 ? 1 : 0);
    },
    // 计算总费用
    calcTotalFee() {
      let total = this.calcDeliveryFee();
      if (this.formData.isUrgent) {
        total += this.urgentFee;
      }
      total += this.calcInsuranceFee();
      return total;
    },
    // 表单验证
    validateForm() {
      if (!this.formData.senderName) {
        common_vendor.index.showToast({
          title: "请输入寄件人姓名",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.senderPhone) {
        common_vendor.index.showToast({
          title: "请输入寄件人电话",
          icon: "none"
        });
        return false;
      }
      if (!/^1\d{10}$/.test(this.formData.senderPhone)) {
        common_vendor.index.showToast({
          title: "寄件人电话格式不正确",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.senderAddress) {
        common_vendor.index.showToast({
          title: "请输入寄件人地址",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.receiverName) {
        common_vendor.index.showToast({
          title: "请输入收件人姓名",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.receiverPhone) {
        common_vendor.index.showToast({
          title: "请输入收件人电话",
          icon: "none"
        });
        return false;
      }
      if (!/^1\d{10}$/.test(this.formData.receiverPhone)) {
        common_vendor.index.showToast({
          title: "收件人电话格式不正确",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.receiverAddress) {
        common_vendor.index.showToast({
          title: "请输入收件人地址",
          icon: "none"
        });
        return false;
      }
      if (parseFloat(this.formData.weight) <= 0) {
        common_vendor.index.showToast({
          title: "请输入有效的包裹重量",
          icon: "none"
        });
        return false;
      }
      if (parseFloat(this.formData.insuranceValue) < 0 || parseFloat(this.formData.insuranceValue) > 3e3) {
        common_vendor.index.showToast({
          title: "保价金额不能超过3000元",
          icon: "none"
        });
        return false;
      }
      return true;
    },
    // 提交订单
    submitOrder() {
      if (!this.validateForm()) {
        return;
      }
      common_vendor.index.showModal({
        title: "确认提交",
        content: `订单总金额：¥${this.calcTotalFee().toFixed(2)}，确认提交订单？`,
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.showLoading({
              title: "提交中...",
              mask: true
            });
            setTimeout(() => {
              common_vendor.index.hideLoading();
              common_vendor.index.navigateTo({
                url: `/pages/payment/payment?amount=${this.calcTotalFee().toFixed(2)}&method=${this.formData.paymentMethod}`,
                success: (res2) => {
                  res2.eventChannel.emit("orderData", {
                    ...this.formData,
                    deliveryFee: this.calcDeliveryFee(),
                    insuranceFee: this.calcInsuranceFee(),
                    totalFee: this.calcTotalFee(),
                    orderTime: (/* @__PURE__ */ new Date()).toLocaleString()
                  });
                }
              });
            }, 1e3);
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
    a: common_vendor.o(($event) => $options.showAddressBook("sender")),
    b: $data.formData.senderName,
    c: common_vendor.o(($event) => $data.formData.senderName = $event.detail.value),
    d: $data.formData.senderPhone,
    e: common_vendor.o(($event) => $data.formData.senderPhone = $event.detail.value),
    f: $data.formData.senderAddress,
    g: common_vendor.o(($event) => $data.formData.senderAddress = $event.detail.value),
    h: common_vendor.o(($event) => $options.showAddressBook("receiver")),
    i: $data.formData.receiverName,
    j: common_vendor.o(($event) => $data.formData.receiverName = $event.detail.value),
    k: $data.formData.receiverPhone,
    l: common_vendor.o(($event) => $data.formData.receiverPhone = $event.detail.value),
    m: $data.formData.receiverAddress,
    n: common_vendor.o(($event) => $data.formData.receiverAddress = $event.detail.value),
    o: common_vendor.t($data.packageTypes[$data.formData.packageType]),
    p: common_vendor.p({
      type: "arrowright",
      size: "14",
      color: "#999"
    }),
    q: common_vendor.o((...args) => $options.handlePackageTypeChange && $options.handlePackageTypeChange(...args)),
    r: $data.formData.packageType,
    s: $data.packageTypes,
    t: $data.formData.weight,
    v: common_vendor.o(($event) => $data.formData.weight = $event.detail.value),
    w: $data.formData.insuranceValue,
    x: common_vendor.o(($event) => $data.formData.insuranceValue = $event.detail.value),
    y: $data.formData.note,
    z: common_vendor.o(($event) => $data.formData.note = $event.detail.value),
    A: $data.formData.isUrgent,
    B: common_vendor.o((e) => $data.formData.isUrgent = e.detail.value),
    C: common_vendor.o(($event) => $data.formData.isUrgent = !$data.formData.isUrgent),
    D: $data.formData.needReceipt,
    E: common_vendor.o((e) => $data.formData.needReceipt = e.detail.value),
    F: common_vendor.o(($event) => $data.formData.needReceipt = !$data.formData.needReceipt),
    G: common_vendor.t($options.calcDeliveryFee().toFixed(2)),
    H: $data.formData.isUrgent
  }, $data.formData.isUrgent ? {
    I: common_vendor.t($data.urgentFee.toFixed(2))
  } : {}, {
    J: $data.formData.insuranceValue > 0
  }, $data.formData.insuranceValue > 0 ? {
    K: common_vendor.t($options.calcInsuranceFee().toFixed(2))
  } : {}, {
    L: common_vendor.t($options.calcTotalFee().toFixed(2)),
    M: common_vendor.f($data.paymentMethods, (item, index, i0) => {
      return {
        a: item.icon,
        b: common_vendor.t(item.name),
        c: item.value,
        d: $data.formData.paymentMethod === item.value,
        e: index
      };
    }),
    N: common_vendor.o((...args) => $options.handlePaymentChange && $options.handlePaymentChange(...args)),
    O: common_vendor.t($options.calcTotalFee().toFixed(2)),
    P: common_vendor.o((...args) => $options.submitOrder && $options.submitOrder(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
