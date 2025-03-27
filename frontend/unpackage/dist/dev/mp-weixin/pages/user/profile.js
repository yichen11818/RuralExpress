"use strict";
const common_vendor = require("../../common/vendor.js");
const api_user = require("../../api/user.js");
const _sfc_main = {
  data() {
    return {
      userInfo: {},
      genderArray: ["男", "女", "保密"],
      genderIndex: 2,
      statusBarHeight: 20,
      // 默认值
      isLoading: false
    };
  },
  onLoad() {
    this.getStatusBarHeight();
    this.fetchUserProfile();
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
    // 获取用户资料
    fetchUserProfile() {
      this.isLoading = true;
      common_vendor.index.showLoading({
        title: "加载中..."
      });
      api_user.getUserProfile().then((res) => {
        if (res.code === 200 && res.data) {
          this.userInfo = res.data;
          if (this.userInfo.gender !== void 0) {
            this.genderIndex = this.userInfo.gender;
          }
        } else {
          common_vendor.index.showToast({
            title: "获取用户信息失败",
            icon: "none"
          });
        }
      }).catch((err) => {
        console.error("获取用户信息失败", err);
        common_vendor.index.showToast({
          title: "获取用户信息失败",
          icon: "none"
        });
      }).finally(() => {
        common_vendor.index.hideLoading();
        this.isLoading = false;
      });
    },
    // 选择头像
    chooseAvatar() {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          this.userInfo.avatar = tempFilePath;
          this.uploadAvatar(tempFilePath);
        }
      });
    },
    // 上传头像
    uploadAvatar(filePath) {
      common_vendor.index.showLoading({
        title: "上传中..."
      });
      common_vendor.index.uploadFile({
        url: "http://localhost:8080/api/file/upload",
        filePath,
        name: "file",
        header: {
          "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`
        },
        formData: {
          "type": "avatar"
        },
        success: (uploadRes) => {
          try {
            const response = JSON.parse(uploadRes.data);
            if (response.code === 200 && response.data) {
              this.userInfo.avatar = response.data.url;
              common_vendor.index.showToast({
                title: "上传成功",
                icon: "success"
              });
            } else {
              common_vendor.index.showToast({
                title: response.message || "上传失败",
                icon: "none"
              });
            }
          } catch (e) {
            common_vendor.index.showToast({
              title: "上传失败",
              icon: "none"
            });
          }
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "上传失败",
            icon: "none"
          });
        },
        complete: () => {
          common_vendor.index.hideLoading();
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
      if (this.isLoading)
        return;
      common_vendor.index.showLoading({
        title: "保存中..."
      });
      this.isLoading = true;
      api_user.updateUserProfile(this.userInfo).then((res) => {
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "保存成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.navigateBack();
          }, 1500);
        } else {
          common_vendor.index.showToast({
            title: res.message || "保存失败",
            icon: "none"
          });
        }
      }).catch((err) => {
        console.error("保存用户信息失败", err);
        common_vendor.index.showToast({
          title: "保存失败",
          icon: "none"
        });
      }).finally(() => {
        common_vendor.index.hideLoading();
        this.isLoading = false;
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
