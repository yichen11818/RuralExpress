"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const api_home = require("../../api/home.js");
const common_assets = require("../../common/assets.js");
const uniIcons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const _sfc_main = {
  components: {
    uniIcons
  },
  data() {
    return {
      loading: true,
      banners: [],
      notices: [],
      nearestCouriers: [],
      userLocation: null,
      locationFailed: false,
      recentOrders: [],
      packageTypes: ["小件", "中件", "大件"],
      selectedPackageType: 0,
      distance: 10,
      calculatedPrice: 0
    };
  },
  onLoad() {
    if (!api_auth.isLoggedIn()) {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
      return;
    }
    this.getUserLocation();
    this.calculatePrice();
  },
  onPullDownRefresh() {
    common_vendor.index.removeStorageSync("_home_loaded_");
    if (this.userLocation && !this.locationFailed) {
      this.loadNearbyCouriers();
      this.loadHomeData(() => {
        common_vendor.index.showToast({
          title: "刷新成功",
          icon: "success",
          duration: 1500
        });
      });
    } else {
      this.getUserLocation();
    }
  },
  methods: {
    // 获取用户位置
    getUserLocation() {
      let loadingClosed = false;
      common_vendor.index.getSetting({
        success: (res) => {
          if (res.authSetting["scope.userLocation"]) {
            common_vendor.index.showLoading({
              title: "定位中..."
            });
            const timeout = setTimeout(() => {
              if (!loadingClosed) {
                loadingClosed = true;
                common_vendor.index.hideLoading();
              }
            }, 1e4);
            this.getLocationInfo(loadingClosed, timeout);
          } else {
            common_vendor.index.showModal({
              title: "位置信息",
              content: "为了向您提供附近的快递员服务，我们需要获取您的位置信息",
              success: (modalRes) => {
                if (modalRes.confirm) {
                  this.requestLocationPermission(loadingClosed);
                } else {
                  this.handleLocationFailed("您拒绝了位置授权，将显示推荐快递员");
                }
              }
            });
          }
        },
        fail: () => {
          this.handleLocationFailed("获取权限信息失败，将显示推荐快递员");
        }
      });
    },
    // 申请位置权限
    requestLocationPermission(loadingClosed) {
      common_vendor.index.authorize({
        scope: "scope.userLocation",
        success: () => {
          common_vendor.index.showLoading({
            title: "定位中..."
          });
          const timeout = setTimeout(() => {
            if (!loadingClosed) {
              loadingClosed = true;
              common_vendor.index.hideLoading();
            }
          }, 1e4);
          this.getLocationInfo(loadingClosed, timeout);
        },
        fail: () => {
          common_vendor.index.showModal({
            title: "提示",
            content: "获取位置权限失败，您可以在系统设置中开启位置权限",
            confirmText: "去设置",
            cancelText: "取消",
            success: (modalRes) => {
              if (modalRes.confirm) {
                common_vendor.index.openSetting({
                  success: (settingRes) => {
                    if (settingRes.authSetting["scope.userLocation"]) {
                      common_vendor.index.showLoading({
                        title: "定位中..."
                      });
                      const timeout = setTimeout(() => {
                        if (!loadingClosed) {
                          loadingClosed = true;
                          common_vendor.index.hideLoading();
                        }
                      }, 1e4);
                      this.getLocationInfo(loadingClosed, timeout);
                    } else {
                      this.handleLocationFailed("您拒绝了位置授权，将显示推荐快递员");
                    }
                  }
                });
              } else {
                this.handleLocationFailed("您拒绝了位置授权，将显示推荐快递员");
              }
            }
          });
        }
      });
    },
    // 获取位置信息
    getLocationInfo(loadingClosed, timeout) {
      common_vendor.index.getLocation({
        type: "wgs84",
        success: (res) => {
          console.log("获取位置成功", res);
          const { latitude, longitude } = res;
          this.userLocation = { latitude, longitude };
          this.loadNearbyCouriers();
          this.loadHomeData();
        },
        fail: (err) => {
          console.error("获取位置失败", err);
          this.handleLocationFailed();
        },
        complete: () => {
          if (!loadingClosed) {
            loadingClosed = true;
            common_vendor.index.hideLoading();
            clearTimeout(timeout);
          }
        }
      });
    },
    // 处理位置获取失败
    handleLocationFailed(message) {
      console.error("位置获取失败");
      this.locationFailed = true;
      if (message) {
        common_vendor.index.showToast({
          title: message,
          icon: "none",
          duration: 3e3
        });
      } else {
        common_vendor.index.showToast({
          title: "获取位置信息失败，将显示推荐快递员",
          icon: "none",
          duration: 2e3
        });
      }
      this.loadHomeData();
    },
    // 加载附近快递员
    loadNearbyCouriers() {
      if (!this.userLocation)
        return;
      const { latitude, longitude } = this.userLocation;
      api_home.getNearestCouriers(5, latitude, longitude).then((res) => {
        console.log("附近快递员响应:", res);
        if (res && res.code === 200 && res.data) {
          this.nearestCouriers = res.data.map((courier) => {
            const courierNames = {
              1: "张师傅",
              2: "李师傅",
              3: "王师傅",
              4: "刘师傅",
              5: "赵师傅"
            };
            const courierAvatars = {
              1: "/static/images/courier-1.png",
              2: "/static/images/courier-2.png",
              3: "/static/images/courier-3.png",
              4: "/static/images/courier-4.png",
              5: "/static/images/courier-5.png"
            };
            return {
              id: courier.id,
              name: courierNames[courier.userId] || `快递员${courier.userId}`,
              avatar: courierAvatars[courier.userId] || "/static/images/default-avatar.png",
              rating: parseFloat(courier.rating) || 5,
              completedOrders: courier.completedOrders || 0,
              // 添加距离信息
              distance: courier.distance ? `${courier.distance}公里` : "未知距离"
            };
          });
        }
      }).catch((err) => {
        console.error("获取附近快递员失败", err);
      });
    },
    // 加载首页数据
    loadHomeData(callback) {
      this.loading = true;
      const showLoading = !common_vendor.index.getStorageSync("_home_loaded_");
      if (showLoading) {
        common_vendor.index.showLoading({
          title: "加载数据中..."
        });
      }
      api_home.getHomeData().then((res) => {
        console.log("首页数据响应:", res);
        if (res && res.code === 200 && res.data) {
          const data = res.data;
          this.banners = data.banners || [];
          this.notices = data.notices || [];
          if (!this.userLocation || this.nearestCouriers.length === 0) {
            this.nearestCouriers = (data.nearestCouriers || []).map((courier) => {
              const courierNames = {
                1: "张师傅",
                2: "李师傅",
                3: "王师傅",
                4: "刘师傅",
                5: "赵师傅"
              };
              const courierAvatars = {
                1: "/static/images/courier1.jpg",
                2: "/static/images/courier2.jpg",
                3: "/static/images/courier3.jpg",
                4: "/static/images/courier4.jpg",
                5: "/static/images/courier5.jpg"
              };
              return {
                id: courier.id,
                name: courierNames[courier.userId] || `快递员${courier.userId}`,
                avatar: courierAvatars[courier.userId] || "/static/images/default-avatar.png",
                rating: parseFloat(courier.rating) || 5,
                completedOrders: courier.completedOrders || 0
              };
            });
          }
          this.recentOrders = data.recentOrders || [];
          common_vendor.index.setStorageSync("_home_loaded_", true);
        } else {
          console.warn("首页数据返回格式不正确:", res);
          common_vendor.index.showToast({
            title: "数据加载异常",
            icon: "none"
          });
        }
      }).catch((err) => {
        console.error("获取首页数据失败", err);
        common_vendor.index.showToast({
          title: "数据加载失败",
          icon: "none"
        });
      }).finally(() => {
        if (showLoading) {
          common_vendor.index.hideLoading();
        }
        this.loading = false;
        if (typeof callback === "function") {
          callback();
        }
        common_vendor.index.stopPullDownRefresh();
      });
    },
    // 清空数据
    clearData() {
      this.banners = [];
      this.notices = [];
      this.nearestCouriers = [];
      this.recentOrders = [];
    },
    // 页面导航
    navigateTo(url) {
      common_vendor.index.navigateTo({
        url
      });
    },
    // 处理轮播图点击
    handleBannerClick(banner) {
      if (banner.linkUrl) {
        this.navigateTo(banner.linkUrl);
      }
    },
    // 处理公告点击
    handleNoticeClick(notice) {
      if (notice.linkUrl) {
        this.navigateTo(notice.linkUrl);
      }
    },
    // 获取订单状态文本
    getOrderStatusText(status) {
      const statusTexts = {
        "pending": "待发货",
        "shipped": "已发货",
        "delivered": "已送达",
        "cancelled": "已取消"
      };
      return statusTexts[status] || "未知状态";
    },
    // 格式化日期
    formatDate(dateStr) {
      const date = new Date(dateStr);
      return date.toLocaleDateString() + " " + date.toLocaleTimeString();
    },
    // 处理包裹类型变化
    handlePackageTypeChange(e) {
      this.selectedPackageType = e.detail.value;
      this.calculatePrice();
    },
    // 处理距离变化
    handleDistanceChange(e) {
      this.distance = e.detail.value;
      this.calculatePrice();
    },
    // 计算价格
    calculatePrice() {
      const basePrice = 5;
      const distanceFee = Math.max(0, this.distance - 2) * 1;
      let packageTypeFee = 0;
      switch (parseInt(this.selectedPackageType)) {
        case 0:
          packageTypeFee = 0;
          break;
        case 1:
          packageTypeFee = 3;
          break;
        case 2:
          packageTypeFee = 6;
          break;
      }
      this.calculatedPrice = basePrice + distanceFee + packageTypeFee;
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
    a: $data.loading
  }, $data.loading ? {
    b: common_vendor.f(4, (i, k0, i0) => {
      return {
        a: i
      };
    }),
    c: common_vendor.f(4, (i, k0, i0) => {
      return {
        a: i
      };
    }),
    d: common_vendor.f(2, (i, k0, i0) => {
      return {
        a: i
      };
    })
  } : common_vendor.e({
    e: common_vendor.p({
      type: "search",
      size: "18",
      color: "#999"
    }),
    f: common_vendor.o(($event) => $options.navigateTo("/pages/search/search")),
    g: common_vendor.f($data.banners, (item, index, i0) => {
      return {
        a: item.imageUrl,
        b: index,
        c: common_vendor.o(($event) => $options.handleBannerClick(item), index)
      };
    }),
    h: common_assets._imports_0,
    i: common_vendor.o(($event) => $options.navigateTo("/pages/delivery/send")),
    j: common_assets._imports_1,
    k: common_vendor.o(($event) => $options.navigateTo("/pages/delivery/receive")),
    l: common_assets._imports_2,
    m: common_vendor.o(($event) => $options.navigateTo("/pages/order/track")),
    n: common_assets._imports_3,
    o: common_vendor.o(($event) => $options.navigateTo("/pages/courier/recruitment")),
    p: common_vendor.p({
      type: "notification-filled",
      size: "18",
      color: "#FF6B35"
    }),
    q: common_vendor.f($data.notices, (item, index, i0) => {
      return {
        a: common_vendor.t(item.content),
        b: index,
        c: common_vendor.o(($event) => $options.handleNoticeClick(item), index)
      };
    }),
    r: $data.nearestCouriers.length > 0
  }, $data.nearestCouriers.length > 0 ? {
    s: common_vendor.t($data.userLocation ? "附近快递员" : "推荐快递员"),
    t: common_vendor.p({
      type: "right",
      size: "14",
      color: "#999"
    }),
    v: common_vendor.o(($event) => $options.navigateTo("/pages/courier/list")),
    w: common_vendor.f($data.nearestCouriers, (item, index, i0) => {
      return common_vendor.e({
        a: item.avatar || "/static/images/default-avatar.png",
        b: common_vendor.t(item.name || "快递员" + (index + 1)),
        c: "1a526eb5-3-" + i0,
        d: common_vendor.t((item.rating || 5).toFixed(1)),
        e: common_vendor.t(item.completedOrders || 0),
        f: item.distance
      }, item.distance ? {
        g: "1a526eb5-4-" + i0,
        h: common_vendor.p({
          type: "location",
          size: "12",
          color: "#999"
        }),
        i: common_vendor.t(item.distance)
      } : {}, {
        j: index,
        k: common_vendor.o(($event) => $options.navigateTo(`/pages/courier/detail?id=${item.id}`), index)
      });
    }),
    x: common_vendor.p({
      type: "star-filled",
      size: "12",
      color: "#FFAC33"
    })
  } : {}, {
    y: $data.recentOrders.length > 0
  }, $data.recentOrders.length > 0 ? {
    z: common_vendor.p({
      type: "right",
      size: "14",
      color: "#999"
    }),
    A: common_vendor.o(($event) => $options.navigateTo("/pages/order/order")),
    B: common_vendor.f($data.recentOrders, (item, index, i0) => {
      return {
        a: common_vendor.t($options.getOrderStatusText(item.status)),
        b: common_vendor.n("status-" + item.status),
        c: common_vendor.t($options.formatDate(item.createdAt)),
        d: common_vendor.t(item.senderAddress),
        e: "1a526eb5-6-" + i0,
        f: common_vendor.t(item.receiverAddress),
        g: "1a526eb5-7-" + i0,
        h: index,
        i: common_vendor.o(($event) => $options.navigateTo(`/pages/order/detail?id=${item.id}`), index)
      };
    }),
    C: common_vendor.p({
      type: "arrowdown",
      size: "14",
      color: "#ddd"
    }),
    D: common_vendor.p({
      type: "right",
      size: "16",
      color: "#C8C8C8"
    })
  } : {}, {
    E: common_vendor.t($data.packageTypes[$data.selectedPackageType]),
    F: common_vendor.p({
      type: "arrowdown",
      size: "14",
      color: "#999"
    }),
    G: $data.packageTypes,
    H: common_vendor.o((...args) => $options.handlePackageTypeChange && $options.handlePackageTypeChange(...args)),
    I: $data.distance,
    J: common_vendor.o((...args) => $options.handleDistanceChange && $options.handleDistanceChange(...args)),
    K: common_vendor.t($data.distance),
    L: common_vendor.t($data.calculatedPrice.toFixed(2)),
    M: common_vendor.o(($event) => $options.navigateTo("/pages/delivery/send")),
    N: common_vendor.p({
      type: "checkmarkempty",
      size: "16",
      color: "#FF6B35"
    }),
    O: common_vendor.p({
      type: "checkmarkempty",
      size: "16",
      color: "#FF6B35"
    }),
    P: common_vendor.p({
      type: "checkmarkempty",
      size: "16",
      color: "#FF6B35"
    })
  }));
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
