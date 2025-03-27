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
      nearestCouriers: []
    };
  },
  onLoad() {
    if (!api_auth.isLoggedIn()) {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
      return;
    }
    this.loadHomeData();
  },
  onPullDownRefresh() {
    this.loadHomeData(() => {
      common_vendor.index.stopPullDownRefresh();
      common_vendor.index.showToast({
        title: "刷新成功",
        icon: "success"
      });
    });
  },
  methods: {
    // 加载首页数据
    loadHomeData(callback) {
      this.loading = true;
      api_home.getHomeData().then((res) => {
        console.log("首页数据响应:", res);
        if (res && res.data) {
          const data = res.data;
          this.banners = data.banners || [];
          this.notices = data.notices || [];
          this.nearestCouriers = data.nearestCouriers || [];
          this.nearestCouriers = this.nearestCouriers.map((courier) => {
            return {
              id: courier.id,
              name: courier.name || courier.userName || "未知快递员",
              avatar: courier.avatar || "/static/images/default-avatar.png",
              rating: courier.rating || 5,
              completedOrders: courier.completedOrders || 0
            };
          });
        } else {
          console.warn("首页数据返回格式不正确:", res);
          this.useDefaultData();
        }
      }).catch((err) => {
        console.error("获取首页数据失败", err);
        if (err && typeof err === "object" && (err.banners || err.notices || err.nearestCouriers)) {
          console.warn("异常中包含有效数据，尝试使用");
          if (err.banners)
            this.banners = err.banners;
          if (err.notices)
            this.notices = err.notices;
          if (err.nearestCouriers) {
            this.nearestCouriers = err.nearestCouriers.map((courier) => {
              return {
                id: courier.id,
                name: courier.name || courier.userName || "未知快递员",
                avatar: courier.avatar || "/static/images/default-avatar.png",
                rating: courier.rating || 5,
                completedOrders: courier.completedOrders || 0
              };
            });
          }
        } else {
          common_vendor.index.showToast({
            title: "数据加载失败",
            icon: "none"
          });
          this.useDefaultData();
        }
      }).finally(() => {
        this.loading = false;
        callback && callback();
      });
    },
    // 使用默认数据（当API请求失败时）
    useDefaultData() {
      this.banners = [
        {
          imageUrl: "/static/images/banner1.jpg",
          linkUrl: "/pages/notice/detail?id=1",
          title: "乡递通平台上线啦"
        },
        {
          imageUrl: "/static/images/banner2.jpg",
          linkUrl: "/pages/activity/detail?id=1",
          title: "快递送货优惠活动"
        },
        {
          imageUrl: "/static/images/banner3.jpg",
          linkUrl: "/pages/courier/recruitment",
          title: "快递员招募计划"
        }
      ];
      this.notices = [
        {
          id: 1,
          content: "乡递通平台正式上线，为乡村快递提供便捷服务",
          linkUrl: "/pages/notice/detail?id=1"
        },
        {
          id: 2,
          content: "成为快递员，每单收益最高可达10元",
          linkUrl: "/pages/courier/recruitment"
        },
        {
          id: 3,
          content: "乡递通招募村镇快递员，详情查看招募页面",
          linkUrl: "/pages/notice/detail?id=3"
        }
      ];
      this.nearestCouriers = [
        {
          id: 1,
          name: "张师傅",
          avatar: "/static/images/courier1.jpg",
          rating: 4.9,
          completedOrders: 326
        },
        {
          id: 2,
          name: "李师傅",
          avatar: "/static/images/courier2.jpg",
          rating: 4.8,
          completedOrders: 215
        },
        {
          id: 3,
          name: "王师傅",
          avatar: "/static/images/courier3.jpg",
          rating: 4.7,
          completedOrders: 198
        }
      ];
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
    a: common_vendor.p({
      type: "search",
      size: "18",
      color: "#666"
    }),
    b: common_vendor.o(($event) => $options.navigateTo("/pages/search/search")),
    c: common_vendor.f($data.banners, (item, index, i0) => {
      return {
        a: item.imageUrl,
        b: index,
        c: common_vendor.o(($event) => $options.handleBannerClick(item), index)
      };
    }),
    d: common_assets._imports_0,
    e: common_vendor.o(($event) => $options.navigateTo("/pages/delivery/send")),
    f: common_assets._imports_1,
    g: common_vendor.o(($event) => $options.navigateTo("/pages/delivery/receive")),
    h: common_assets._imports_2,
    i: common_vendor.o(($event) => $options.navigateTo("/pages/order/track")),
    j: common_assets._imports_3,
    k: common_vendor.o(($event) => $options.navigateTo("/pages/courier/recruitment")),
    l: common_vendor.p({
      type: "notification",
      size: "18",
      color: "#3cc51f"
    }),
    m: common_vendor.f($data.notices, (item, index, i0) => {
      return {
        a: common_vendor.t(item.content),
        b: index,
        c: common_vendor.o(($event) => $options.handleNoticeClick(item), index)
      };
    }),
    n: $data.nearestCouriers.length > 0
  }, $data.nearestCouriers.length > 0 ? {
    o: common_vendor.o(($event) => $options.navigateTo("/pages/courier/list")),
    p: common_vendor.f($data.nearestCouriers, (item, index, i0) => {
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
    q: common_vendor.p({
      type: "star-filled",
      size: "12",
      color: "#ff9900"
    })
  } : {}, {
    r: common_vendor.p({
      type: "checkmarkempty",
      size: "20",
      color: "#3cc51f"
    }),
    s: common_vendor.p({
      type: "checkmarkempty",
      size: "20",
      color: "#3cc51f"
    }),
    t: common_vendor.p({
      type: "checkmarkempty",
      size: "20",
      color: "#3cc51f"
    }),
    v: $data.loading
  }, $data.loading ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
