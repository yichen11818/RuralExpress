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
    this.getUserLocation();
  },
  methods: {
    // 获取用户位置
    getUserLocation() {
      common_vendor.index.showLoading({
        title: "定位中..."
      });
      common_vendor.index.getLocation({
        type: "wgs84",
        success: (res) => {
          const { latitude, longitude } = res;
          this.userLocation = { latitude, longitude };
          console.log("获取位置成功", this.userLocation);
          this.loadNearbyCouriers();
          this.loadHomeData();
        },
        fail: (err) => {
          console.error("获取位置失败", err);
          this.locationFailed = true;
          common_vendor.index.showToast({
            title: "获取位置信息失败，将显示推荐快递员",
            icon: "none",
            duration: 2e3
          });
          this.loadHomeData();
        },
        complete: () => {
          common_vendor.index.hideLoading();
        }
      });
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
            return {
              id: courier.id,
              name: courier.name || courier.userName || "未知快递员",
              avatar: courier.avatar || "/static/images/default-avatar.png",
              rating: courier.rating || 5,
              completedOrders: courier.completedOrders || 0
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
      api_home.getHomeData().then((res) => {
        console.log("首页数据响应:", res);
        if (res && res.code === 200 && res.data) {
          const data = res.data;
          this.banners = data.banners || [];
          this.notices = data.notices || [];
          if (!this.userLocation || this.nearestCouriers.length === 0) {
            this.nearestCouriers = (data.nearestCouriers || []).map((courier) => {
              return {
                id: courier.id,
                name: courier.name || courier.userName || "未知快递员",
                avatar: courier.avatar || "/static/images/default-avatar.png",
                rating: courier.rating || 5,
                completedOrders: courier.completedOrders || 0
              };
            });
          }
          this.recentOrders = data.recentOrders || [];
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
      color: "#666"
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
      type: "notification",
      size: "18",
      color: "#3cc51f"
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
    t: common_vendor.o(($event) => $options.navigateTo("/pages/courier/list")),
    v: common_vendor.f($data.nearestCouriers, (item, index, i0) => {
      return {
        a: item.avatar || "/static/images/default-avatar.png",
        b: common_vendor.t(item.name),
        c: "1a526eb5-2-" + i0,
        d: common_vendor.t(item.rating),
        e: common_vendor.t(item.completedOrders),
        f: index,
        g: common_vendor.o(($event) => $options.navigateTo(`/pages/courier/detail?id=${item.id}`), index)
      };
    }),
    w: common_vendor.p({
      type: "star-filled",
      size: "12",
      color: "#ff9900"
    })
  } : {}, {
    x: $data.recentOrders.length > 0
  }, $data.recentOrders.length > 0 ? {
    y: common_vendor.o(($event) => $options.navigateTo("/pages/order/order")),
    z: common_vendor.f($data.recentOrders, (item, index, i0) => {
      return {
        a: common_vendor.t($options.getOrderStatusText(item.status)),
        b: common_vendor.n("status-" + item.status),
        c: common_vendor.t($options.formatDate(item.createdAt)),
        d: common_vendor.t(item.senderAddress),
        e: common_vendor.t(item.receiverAddress),
        f: "1a526eb5-3-" + i0,
        g: index,
        h: common_vendor.o(($event) => $options.navigateTo(`/pages/order/detail?id=${item.id}`), index)
      };
    }),
    A: common_vendor.p({
      type: "right",
      size: "16",
      color: "#999"
    })
  } : {}, {
    B: common_vendor.t($data.packageTypes[$data.selectedPackageType]),
    C: common_vendor.p({
      type: "arrowdown",
      size: "14",
      color: "#666"
    }),
    D: $data.packageTypes,
    E: common_vendor.o((...args) => $options.handlePackageTypeChange && $options.handlePackageTypeChange(...args)),
    F: $data.distance,
    G: common_vendor.o((...args) => $options.handleDistanceChange && $options.handleDistanceChange(...args)),
    H: common_vendor.t($data.distance),
    I: common_vendor.t($data.calculatedPrice.toFixed(2)),
    J: common_vendor.o(($event) => $options.navigateTo("/pages/delivery/send")),
    K: common_vendor.p({
      type: "checkmarkempty",
      size: "20",
      color: "#3cc51f"
    }),
    L: common_vendor.p({
      type: "checkmarkempty",
      size: "20",
      color: "#3cc51f"
    }),
    M: common_vendor.p({
      type: "checkmarkempty",
      size: "20",
      color: "#3cc51f"
    })
  }));
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
