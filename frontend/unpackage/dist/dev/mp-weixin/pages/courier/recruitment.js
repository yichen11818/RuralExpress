"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const uniIcons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const _sfc_main = {
  components: {
    uniIcons
  },
  data() {
    return {
      submitting: false,
      vehicleOptions: ["电动车", "摩托车", "三轮车", "小汽车", "其他"],
      formData: {
        name: "",
        phone: "",
        idCard: "",
        region: null,
        address: "",
        vehicle: "",
        idCardFront: "",
        idCardBack: "",
        agreement: false
      }
    };
  },
  onLoad() {
    if (!api_auth.isLoggedIn()) {
      common_vendor.index.showModal({
        title: "提示",
        content: "请先登录后再申请成为快递员",
        showCancel: false,
        success: () => {
          common_vendor.index.navigateTo({
            url: "/pages/login/login"
          });
        }
      });
    }
  },
  methods: {
    // 选择服务区域
    regionChange(e) {
      this.formData.region = e.detail.value;
    },
    // 选择交通工具
    vehicleChange(e) {
      this.formData.vehicle = this.vehicleOptions[e.detail.value];
    },
    // 选择身份证图片
    chooseImage(type) {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          if (type === "front") {
            this.formData.idCardFront = res.tempFilePaths[0];
          } else {
            this.formData.idCardBack = res.tempFilePaths[0];
          }
        }
      });
    },
    // 协议勾选
    agreementChange(e) {
      this.formData.agreement = e.detail.value.length > 0;
    },
    // 提交申请
    submitApplication() {
      if (!this.formData.name) {
        return common_vendor.index.showToast({ title: "请输入姓名", icon: "none" });
      }
      if (!this.formData.phone) {
        return common_vendor.index.showToast({ title: "请输入手机号", icon: "none" });
      }
      if (!/^1\d{10}$/.test(this.formData.phone)) {
        return common_vendor.index.showToast({ title: "手机号格式不正确", icon: "none" });
      }
      if (!this.formData.idCard) {
        return common_vendor.index.showToast({ title: "请输入身份证号", icon: "none" });
      }
      if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(this.formData.idCard)) {
        return common_vendor.index.showToast({ title: "身份证号格式不正确", icon: "none" });
      }
      if (!this.formData.region) {
        return common_vendor.index.showToast({ title: "请选择服务区域", icon: "none" });
      }
      if (!this.formData.address) {
        return common_vendor.index.showToast({ title: "请输入详细地址", icon: "none" });
      }
      if (!this.formData.vehicle) {
        return common_vendor.index.showToast({ title: "请选择交通工具", icon: "none" });
      }
      if (!this.formData.idCardFront) {
        return common_vendor.index.showToast({ title: "请上传身份证正面照片", icon: "none" });
      }
      if (!this.formData.idCardBack) {
        return common_vendor.index.showToast({ title: "请上传身份证反面照片", icon: "none" });
      }
      this.submitting = true;
      setTimeout(() => {
        this.submitting = false;
        common_vendor.index.showModal({
          title: "申请提交成功",
          content: "我们会在1-3个工作日内审核您的申请，请保持电话畅通",
          showCancel: false,
          success: () => {
            common_vendor.index.navigateBack();
          }
        });
      }, 1500);
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
    a: $data.formData.name,
    b: common_vendor.o(($event) => $data.formData.name = $event.detail.value),
    c: $data.formData.phone,
    d: common_vendor.o(($event) => $data.formData.phone = $event.detail.value),
    e: $data.formData.idCard,
    f: common_vendor.o(($event) => $data.formData.idCard = $event.detail.value),
    g: $data.formData.region
  }, $data.formData.region ? {
    h: common_vendor.t($data.formData.region[0]),
    i: common_vendor.t($data.formData.region[1]),
    j: common_vendor.t($data.formData.region[2])
  } : {}, {
    k: common_vendor.o((...args) => $options.regionChange && $options.regionChange(...args)),
    l: $data.formData.address,
    m: common_vendor.o(($event) => $data.formData.address = $event.detail.value),
    n: $data.formData.vehicle
  }, $data.formData.vehicle ? {
    o: common_vendor.t($data.formData.vehicle)
  } : {}, {
    p: $data.vehicleOptions,
    q: common_vendor.o((...args) => $options.vehicleChange && $options.vehicleChange(...args)),
    r: $data.formData.idCardFront
  }, $data.formData.idCardFront ? {
    s: $data.formData.idCardFront
  } : {
    t: common_vendor.p({
      type: "camera",
      size: "30",
      color: "#999"
    })
  }, {
    v: common_vendor.o(($event) => $options.chooseImage("front")),
    w: $data.formData.idCardBack
  }, $data.formData.idCardBack ? {
    x: $data.formData.idCardBack
  } : {
    y: common_vendor.p({
      type: "camera",
      size: "30",
      color: "#999"
    })
  }, {
    z: common_vendor.o(($event) => $options.chooseImage("back")),
    A: $data.formData.agreement,
    B: common_vendor.o((...args) => $options.agreementChange && $options.agreementChange(...args)),
    C: common_vendor.t($data.submitting ? "提交中..." : "提交申请"),
    D: common_vendor.o((...args) => $options.submitApplication && $options.submitApplication(...args)),
    E: !$data.formData.agreement || $data.submitting
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
