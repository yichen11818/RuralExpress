"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const api_user = require("../../api/user.js");
const api_courier = require("../../api/courier.js");
const uniIcons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const _sfc_main = {
  components: {
    uniIcons
  },
  data() {
    return {
      submitting: false,
      loading: false,
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
      return;
    }
    this.loadUserProfile();
  },
  methods: {
    // 加载用户信息并预填表单
    loadUserProfile() {
      this.loading = true;
      common_vendor.index.showLoading({
        title: "加载中..."
      });
      api_user.getUserProfile().then((res) => {
        if (res.code === 200 && res.data) {
          const userData = res.data;
          this.formData.name = userData.name || userData.nickname || "";
          this.formData.phone = userData.phone || "";
          if (userData.defaultAddress) {
            const address = userData.defaultAddress;
            if (address.province && address.city && address.district) {
              this.formData.region = [address.province, address.city, address.district];
            }
            this.formData.address = address.detailAddress || "";
          }
        }
      }).catch((err) => {
        console.error("获取用户信息失败", err);
      }).finally(() => {
        common_vendor.index.hideLoading();
        this.loading = false;
      });
    },
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
    // 验证表单
    validateForm() {
      if (!this.formData.name) {
        common_vendor.index.showToast({
          title: "请输入姓名",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.phone) {
        common_vendor.index.showToast({
          title: "请输入手机号",
          icon: "none"
        });
        return false;
      }
      if (!/^1\d{10}$/.test(this.formData.phone)) {
        common_vendor.index.showToast({
          title: "手机号格式不正确",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.idCard) {
        common_vendor.index.showToast({
          title: "请输入身份证号",
          icon: "none"
        });
        return false;
      }
      if (!/^\d{17}[\dXx]$/.test(this.formData.idCard)) {
        common_vendor.index.showToast({
          title: "身份证号格式不正确",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.region) {
        common_vendor.index.showToast({
          title: "请选择服务区域",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.address) {
        common_vendor.index.showToast({
          title: "请输入详细地址",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.vehicle) {
        common_vendor.index.showToast({
          title: "请选择交通工具",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.idCardFront) {
        common_vendor.index.showToast({
          title: "请上传身份证正面照片",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.idCardBack) {
        common_vendor.index.showToast({
          title: "请上传身份证反面照片",
          icon: "none"
        });
        return false;
      }
      if (!this.formData.agreement) {
        common_vendor.index.showToast({
          title: "请阅读并同意服务协议",
          icon: "none"
        });
        return false;
      }
      return true;
    },
    // 上传身份证图片
    uploadIdCardImages() {
      return new Promise((resolve, reject) => {
        const uploadTasks = [];
        uploadTasks.push(
          new Promise((innerResolve, innerReject) => {
            common_vendor.index.uploadFile({
              url: "http://localhost:8080/api/file/upload",
              filePath: this.formData.idCardFront,
              name: "file",
              header: {
                "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`
              },
              formData: {
                "type": "idcard"
              },
              success: (uploadRes) => {
                try {
                  const response = JSON.parse(uploadRes.data);
                  if (response.code === 200 && response.data) {
                    innerResolve(response.data.url);
                  } else {
                    innerReject(new Error(response.message || "上传身份证正面失败"));
                  }
                } catch (e) {
                  innerReject(new Error("上传身份证正面失败"));
                }
              },
              fail: () => {
                innerReject(new Error("上传身份证正面失败"));
              }
            });
          })
        );
        uploadTasks.push(
          new Promise((innerResolve, innerReject) => {
            common_vendor.index.uploadFile({
              url: "http://localhost:8080/api/file/upload",
              filePath: this.formData.idCardBack,
              name: "file",
              header: {
                "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`
              },
              formData: {
                "type": "idcard"
              },
              success: (uploadRes) => {
                try {
                  const response = JSON.parse(uploadRes.data);
                  if (response.code === 200 && response.data) {
                    innerResolve(response.data.url);
                  } else {
                    innerReject(new Error(response.message || "上传身份证反面失败"));
                  }
                } catch (e) {
                  innerReject(new Error("上传身份证反面失败"));
                }
              },
              fail: () => {
                innerReject(new Error("上传身份证反面失败"));
              }
            });
          })
        );
        Promise.all(uploadTasks).then((results) => {
          resolve({
            idCardFront: results[0],
            idCardBack: results[1]
          });
        }).catch((error) => {
          reject(error);
        });
      });
    },
    // 提交申请
    submitApplication() {
      if (this.submitting)
        return;
      if (!this.validateForm()) {
        return;
      }
      this.submitting = true;
      common_vendor.index.showLoading({
        title: "提交中...",
        mask: true
      });
      this.uploadIdCardImages().then((idCardUrls) => {
        const courierData = {
          name: this.formData.name,
          phone: this.formData.phone,
          idCard: this.formData.idCard,
          province: this.formData.region[0],
          city: this.formData.region[1],
          district: this.formData.region[2],
          address: this.formData.address,
          vehicle: this.formData.vehicle,
          idCardFrontUrl: idCardUrls.idCardFront,
          idCardBackUrl: idCardUrls.idCardBack
        };
        return api_courier.applyCourier(courierData);
      }).then((res) => {
        if (res.code === 200) {
          common_vendor.index.hideLoading();
          common_vendor.index.showModal({
            title: "申请提交成功",
            content: "您的快递员申请已提交成功，我们将尽快审核，请留意短信或APP通知。",
            showCancel: false,
            success: () => {
              common_vendor.index.navigateBack();
            }
          });
        } else {
          throw new Error(res.message || "申请提交失败");
        }
      }).catch((err) => {
        console.error("提交申请失败", err);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: err.message || "提交失败，请重试",
          icon: "none"
        });
      }).finally(() => {
        this.submitting = false;
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
