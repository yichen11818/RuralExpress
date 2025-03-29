"use strict";
const common_vendor = require("../common/vendor.js");
const _sfc_main = {
  name: "AddressForm",
  props: {
    // 用于编辑时传入的地址数据
    address: {
      type: Object,
      default: () => ({})
    },
    // 操作类型：add（添加）或 edit（编辑）
    type: {
      type: String,
      default: "add"
    }
  },
  data() {
    return {
      formData: {
        id: "",
        name: "",
        phone: "",
        province: "",
        city: "",
        district: "",
        region: [],
        // 用于 picker 数据绑定
        detailAddress: "",
        addressType: "家",
        // 默认家
        isDefault: false
      },
      customTag: "",
      rules: {
        name: {
          rules: [
            {
              required: true,
              errorMessage: "请输入联系人姓名"
            }
          ]
        },
        phone: {
          rules: [
            {
              required: true,
              errorMessage: "请输入联系人手机号码"
            },
            {
              pattern: /^1[3-9]\d{9}$/,
              errorMessage: "手机号码格式不正确"
            }
          ]
        },
        region: {
          rules: [
            {
              required: true,
              errorMessage: "请选择所在地区"
            }
          ]
        },
        detailAddress: {
          rules: [
            {
              required: true,
              errorMessage: "请输入详细地址"
            }
          ]
        }
      }
    };
  },
  created() {
    if (this.type === "edit" && this.address) {
      this.initFormData();
    }
  },
  methods: {
    initFormData() {
      const address = this.address;
      const addressType = this.getAddressTypeText(address.addressType);
      this.formData = {
        id: address.id || "",
        name: address.name || "",
        phone: address.phone || "",
        province: address.province || "",
        city: address.city || "",
        district: address.district || "",
        region: address.province && address.city && address.district ? [address.province, address.city, address.district] : [],
        detailAddress: address.detailAddress || "",
        addressType,
        isDefault: !!address.isDefault
      };
      if (!["家", "公司", "学校"].includes(addressType)) {
        this.customTag = addressType;
      }
    },
    handleRegionChange(e) {
      const region = e.detail.value;
      this.formData.region = region;
      this.formData.province = region[0];
      this.formData.city = region[1];
      this.formData.district = region[2];
    },
    showCustomTag() {
      this.$refs.customTagPopup.open();
    },
    confirmCustomTag(val) {
      this.customTag = val;
      this.formData.addressType = val;
    },
    submitForm() {
      this.$refs.addressForm.validate().then((res) => {
        const addressData = {
          ...this.formData,
          // 确保地址类型字段格式为数字
          addressType: this.getAddressTypeValue(this.formData.addressType)
        };
        this.$emit("submit", addressData);
      }).catch((err) => {
        console.error("表单验证失败", err);
      });
    },
    // 将地址类型文本转换为数值
    getAddressTypeValue(type) {
      const typeMap = {
        "家": 0,
        "公司": 1,
        "学校": 2
      };
      return typeMap[type] !== void 0 ? typeMap[type] : 3;
    },
    // 将数字类型转换为显示文本
    getAddressTypeText(type) {
      const typeMap = {
        0: "家",
        1: "公司",
        2: "学校"
      };
      if (typeof type === "number") {
        return typeMap[type] || "其他";
      }
      if (typeof type === "string" && ["家", "公司", "学校"].includes(type)) {
        return type;
      }
      return "家";
    }
  }
};
if (!Array) {
  const _easycom_uni_easyinput2 = common_vendor.resolveComponent("uni-easyinput");
  const _easycom_uni_forms_item2 = common_vendor.resolveComponent("uni-forms-item");
  const _easycom_uni_forms2 = common_vendor.resolveComponent("uni-forms");
  const _easycom_uni_popup_dialog2 = common_vendor.resolveComponent("uni-popup-dialog");
  const _easycom_uni_popup2 = common_vendor.resolveComponent("uni-popup");
  (_easycom_uni_easyinput2 + _easycom_uni_forms_item2 + _easycom_uni_forms2 + _easycom_uni_popup_dialog2 + _easycom_uni_popup2)();
}
const _easycom_uni_easyinput = () => "../uni_modules/uni-easyinput/components/uni-easyinput/uni-easyinput.js";
const _easycom_uni_forms_item = () => "../uni_modules/uni-forms/components/uni-forms-item/uni-forms-item.js";
const _easycom_uni_forms = () => "../uni_modules/uni-forms/components/uni-forms/uni-forms.js";
const _easycom_uni_popup_dialog = () => "../uni_modules/uni-popup/components/uni-popup-dialog/uni-popup-dialog.js";
const _easycom_uni_popup = () => "../uni_modules/uni-popup/components/uni-popup/uni-popup.js";
if (!Math) {
  (_easycom_uni_easyinput + _easycom_uni_forms_item + _easycom_uni_forms + _easycom_uni_popup_dialog + _easycom_uni_popup)();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o(($event) => $data.formData.name = $event),
    b: common_vendor.p({
      placeholder: "请输入联系人姓名",
      modelValue: $data.formData.name
    }),
    c: common_vendor.p({
      label: "联系人",
      name: "name",
      required: true
    }),
    d: common_vendor.o(($event) => $data.formData.phone = $event),
    e: common_vendor.p({
      placeholder: "请输入联系人手机号码",
      modelValue: $data.formData.phone
    }),
    f: common_vendor.p({
      label: "手机号码",
      name: "phone",
      required: true
    }),
    g: $data.formData.province && $data.formData.city && $data.formData.district
  }, $data.formData.province && $data.formData.city && $data.formData.district ? {
    h: common_vendor.t($data.formData.province + " " + $data.formData.city + " " + $data.formData.district)
  } : {}, {
    i: common_vendor.o((...args) => $options.handleRegionChange && $options.handleRegionChange(...args)),
    j: $data.formData.region,
    k: common_vendor.p({
      label: "所在地区",
      name: "region",
      required: true
    }),
    l: common_vendor.o(($event) => $data.formData.detailAddress = $event),
    m: common_vendor.p({
      type: "textarea",
      placeholder: "请输入详细地址，如街道、门牌号、小区、楼栋号、单元室等",
      maxlength: 100,
      modelValue: $data.formData.detailAddress
    }),
    n: common_vendor.p({
      label: "详细地址",
      name: "detailAddress",
      required: true
    }),
    o: $data.formData.addressType === "家" ? 1 : "",
    p: common_vendor.o(($event) => $data.formData.addressType = "家"),
    q: $data.formData.addressType === "公司" ? 1 : "",
    r: common_vendor.o(($event) => $data.formData.addressType = "公司"),
    s: $data.formData.addressType === "学校" ? 1 : "",
    t: common_vendor.o(($event) => $data.formData.addressType = "学校"),
    v: common_vendor.t($data.customTag || "自定义"),
    w: !["家", "公司", "学校"].includes($data.formData.addressType) ? 1 : "",
    x: common_vendor.o((...args) => $options.showCustomTag && $options.showCustomTag(...args)),
    y: common_vendor.p({
      label: "地址类型",
      name: "addressType"
    }),
    z: $data.formData.isDefault,
    A: common_vendor.o((e) => $data.formData.isDefault = e.detail.value),
    B: common_vendor.p({
      name: "isDefault"
    }),
    C: common_vendor.o((...args) => $options.submitForm && $options.submitForm(...args)),
    D: common_vendor.sr("addressForm", "06d77ea4-0"),
    E: common_vendor.p({
      model: $data.formData,
      rules: $data.rules,
      ["validate-trigger"]: "submit",
      ["err-show-type"]: "toast"
    }),
    F: common_vendor.o($options.confirmCustomTag),
    G: common_vendor.p({
      title: "自定义地址标签",
      mode: "input",
      placeholder: "请输入自定义标签，如'公园'、'健身房'等",
      value: $data.customTag
    }),
    H: common_vendor.sr("customTagPopup", "06d77ea4-10"),
    I: common_vendor.p({
      type: "dialog"
    })
  });
}
const Component = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createComponent(Component);
