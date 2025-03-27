"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const _sfc_main = {
  data() {
    return {
      userInfo: {},
      genderArray: ["男", "女", "保密"],
      genderIndex: 2,
      statusBarHeight: 20
      // 默认值
    };
  },
  onLoad() {
    this.getStatusBarHeight();
    this.userInfo = api_auth.getUserInfo() || {};
    if (this.userInfo.gender !== void 0) {
      this.genderIndex = this.userInfo.gender;
    }
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
    // 选择头像
    chooseAvatar() {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          this.userInfo.avatar = res.tempFilePaths[0];
          common_vendor.index.showToast({
            title: "上传头像功能开发中",
            icon: "none"
          });
        }
      });
    },
    // 性别选择
    bindGenderChange(e) {
      this.genderIndex = e.detail.value;
      this.userInfo.gender = parseInt(e.detail.value);
    },
    // 生日选择
    bindBirthdayChange(e) {
      this.userInfo.birthday = e.detail.value;
    },
    // 更换手机号
    changePhone() {
      common_vendor.index.showToast({
        title: "更换手机号功能开发中",
        icon: "none"
      });
    },
    // 保存资料
    saveProfile() {
      common_vendor.index.showLoading({
        title: "保存中..."
      });
      setTimeout(() => {
        common_vendor.index.hideLoading();
        api_auth.updateUserInfo(this.userInfo);
        common_vendor.index.showToast({
          title: "保存成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.navigateBack();
        }, 1500);
      }, 1e3);
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
    d: common_vendor.o((...args) => $options.saveProfile && $options.saveProfile(...args)),
    e: $data.userInfo.avatar || "/static/images/default-avatar.png",
    f: common_vendor.p({
      type: "camera",
      size: "20",
      color: "#999"
    }),
    g: common_vendor.o((...args) => $options.chooseAvatar && $options.chooseAvatar(...args)),
    h: $data.userInfo.nickname,
    i: common_vendor.o(($event) => $data.userInfo.nickname = $event.detail.value),
    j: common_vendor.t($options.formatPhone($data.userInfo.phone)),
    k: common_vendor.o((...args) => $options.changePhone && $options.changePhone(...args)),
    l: common_vendor.t($data.genderArray[$data.genderIndex]),
    m: common_vendor.o((...args) => $options.bindGenderChange && $options.bindGenderChange(...args)),
    n: $data.genderIndex,
    o: $data.genderArray,
    p: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    }),
    q: common_vendor.t($data.userInfo.birthday || "请选择"),
    r: common_vendor.o((...args) => $options.bindBirthdayChange && $options.bindBirthdayChange(...args)),
    s: $data.userInfo.birthday || "1990-01-01",
    t: common_vendor.p({
      type: "right",
      size: "16",
      color: "#ccc"
    })
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
