"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const _sfc_main = {
  data() {
    return {
      formData: {
        nickname: "",
        avatar: ""
      }
    };
  },
  onLoad(options) {
    if (options && options.userInfo) {
      try {
        const userInfo2 = JSON.parse(decodeURIComponent(options.userInfo));
        this.formData.nickname = userInfo2.nickname || "";
        this.formData.avatar = userInfo2.avatar || "";
        return;
      } catch (e) {
        console.error("解析userInfo参数失败", e);
      }
    }
    const userInfo = api_auth.getUserInfo();
    if (userInfo) {
      this.formData.nickname = userInfo.nickname || "";
      this.formData.avatar = userInfo.avatar || "";
    }
  },
  methods: {
    // 选择头像图片
    chooseImage() {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          common_vendor.index.showLoading({
            title: "处理中...",
            mask: true
          });
          setTimeout(() => {
            this.formData.avatar = tempFilePath;
            common_vendor.index.hideLoading();
          }, 500);
        }
      });
    },
    // 保存个人资料
    saveProfile() {
      if (!this.formData.nickname) {
        common_vendor.index.showToast({
          title: "请输入昵称",
          icon: "none"
        });
        return;
      }
      common_vendor.index.showLoading({
        title: "保存中...",
        mask: true
      });
      const userInfo = api_auth.getUserInfo();
      if (!userInfo || !userInfo.id) {
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "获取用户信息失败",
          icon: "none"
        });
        return;
      }
      const updateData = {
        nickname: this.formData.nickname,
        avatar: this.formData.avatar
      };
      setTimeout(() => {
        const newUserInfo = {
          ...userInfo,
          ...updateData
        };
        common_vendor.index.setStorageSync("userInfo", newUserInfo);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "保存成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.switchTab({
            url: "/pages/index/index"
          });
        }, 1500);
      }, 1e3);
    },
    // 跳过设置
    skipSetup() {
      common_vendor.index.showModal({
        title: "提示",
        content: "您可以稍后在个人中心完善资料，确定跳过吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.switchTab({
              url: "/pages/index/index"
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
    a: $data.formData.avatar || "/static/images/default-avatar.png",
    b: common_vendor.p({
      type: "camera",
      size: "30",
      color: "#fff"
    }),
    c: common_vendor.o((...args) => $options.chooseImage && $options.chooseImage(...args)),
    d: $data.formData.nickname,
    e: common_vendor.o(($event) => $data.formData.nickname = $event.detail.value),
    f: common_vendor.o((...args) => $options.saveProfile && $options.saveProfile(...args)),
    g: common_vendor.o((...args) => $options.skipSetup && $options.skipSetup(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
