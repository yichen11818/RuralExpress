"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const uniIcons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const _sfc_main = {
  components: {
    uniIcons
  },
  data() {
    return {
      loading: true,
      refreshing: false,
      hasMore: true,
      page: 1,
      selectedRegion: null,
      currentFilter: "nearest",
      courierList: [
        {
          id: 1,
          name: "张师傅",
          avatar: "/static/images/courier1.jpg",
          rating: 4.9,
          completedOrders: 326,
          serviceTime: 8,
          serviceArea: "江西省 南昌市 青山湖区",
          tags: ["准时送达", "服务好", "有礼貌"],
          distance: 0.8
        },
        {
          id: 2,
          name: "李师傅",
          avatar: "/static/images/courier2.jpg",
          rating: 4.8,
          completedOrders: 215,
          serviceTime: 6,
          serviceArea: "江西省 南昌市 青山湖区",
          tags: ["送货快", "态度好"],
          distance: 1.2
        },
        {
          id: 3,
          name: "王师傅",
          avatar: "/static/images/courier3.jpg",
          rating: 4.7,
          completedOrders: 198,
          serviceTime: 5,
          serviceArea: "江西省 南昌市 青山湖区",
          tags: ["认真负责", "服务周到"],
          distance: 1.5
        },
        {
          id: 4,
          name: "赵师傅",
          avatar: "/static/images/default-avatar.png",
          rating: 4.6,
          completedOrders: 156,
          serviceTime: 4,
          serviceArea: "江西省 南昌市 青山湖区",
          tags: ["准时送达", "沟通顺畅"],
          distance: 2.1
        },
        {
          id: 5,
          name: "钱师傅",
          avatar: "/static/images/default-avatar.png",
          rating: 4.5,
          completedOrders: 132,
          serviceTime: 3,
          serviceArea: "江西省 南昌市 青山湖区",
          tags: ["服务周到", "耐心"],
          distance: 2.8
        }
      ]
    };
  },
  onLoad() {
    this.getCurrentLocation();
    setTimeout(() => {
      this.loading = false;
    }, 500);
  },
  methods: {
    // 获取当前位置
    getCurrentLocation() {
      this.selectedRegion = ["江西省", "南昌市", "青山湖区"];
    },
    // 地区选择变化
    regionChange(e) {
      this.selectedRegion = e.detail.value;
      this.refreshCourierList();
    },
    // 设置筛选条件
    setFilter(filter) {
      if (this.currentFilter === filter)
        return;
      this.currentFilter = filter;
      this.page = 1;
      this.refreshCourierList();
    },
    // 刷新列表
    refresh() {
      this.refreshing = true;
      this.page = 1;
      setTimeout(() => {
        this.refreshCourierList();
        this.refreshing = false;
        common_vendor.index.showToast({
          title: "刷新成功",
          icon: "success"
        });
      }, 1e3);
    },
    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading)
        return;
      this.loading = true;
      this.page++;
      setTimeout(() => {
        if (this.page > 3) {
          this.hasMore = false;
          this.loading = false;
          return;
        }
        const newCouriers = [
          {
            id: 5 + this.page,
            name: "新增师傅" + this.page,
            avatar: "/static/images/default-avatar.png",
            rating: 4.3,
            completedOrders: 100,
            serviceTime: 2,
            serviceArea: "江西省 南昌市 青山湖区",
            tags: ["服务好"],
            distance: 3 + this.page
          }
        ];
        this.courierList = [...this.courierList, ...newCouriers];
        this.loading = false;
      }, 1e3);
    },
    // 刷新快递员列表
    refreshCourierList() {
      if (this.currentFilter === "nearest") {
        this.courierList.sort((a, b) => a.distance - b.distance);
      } else if (this.currentFilter === "rating") {
        this.courierList.sort((a, b) => b.rating - a.rating);
      } else if (this.currentFilter === "orders") {
        this.courierList.sort((a, b) => b.completedOrders - a.completedOrders);
      }
      this.hasMore = true;
    },
    // 显示搜索页面
    showSearch() {
      common_vendor.index.navigateTo({
        url: "/pages/search/search?type=courier"
      });
    },
    // 导航到快递员详情页
    navigateToDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/courier/detail?id=${id}`
      });
    },
    // 联系快递员
    contactCourier(e) {
      const id = e.currentTarget.dataset.id;
      this.courierList.find((item) => item.id === id);
      common_vendor.index.showActionSheet({
        itemList: ["拨打电话", "发送消息"],
        success: (res) => {
          if (res.tapIndex === 0) {
            common_vendor.index.makePhoneCall({
              phoneNumber: "10086",
              // 这里应该是快递员的电话
              fail: () => {
                common_vendor.index.showToast({
                  title: "拨打电话失败",
                  icon: "none"
                });
              }
            });
          } else if (res.tapIndex === 1) {
            common_vendor.index.showToast({
              title: "消息功能开发中",
              icon: "none"
            });
          }
        }
      });
      return false;
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
      type: "location",
      size: "18",
      color: "#3cc51f"
    }),
    b: $data.selectedRegion
  }, $data.selectedRegion ? {
    c: common_vendor.t($data.selectedRegion[0]),
    d: common_vendor.t($data.selectedRegion[1]),
    e: common_vendor.t($data.selectedRegion[2])
  } : {}, {
    f: common_vendor.o((...args) => $options.regionChange && $options.regionChange(...args)),
    g: common_vendor.p({
      type: "arrowdown",
      size: "14",
      color: "#666"
    }),
    h: common_vendor.p({
      type: "search",
      size: "16",
      color: "#666"
    }),
    i: common_vendor.o((...args) => $options.showSearch && $options.showSearch(...args)),
    j: $data.currentFilter === "nearest" ? 1 : "",
    k: common_vendor.o(($event) => $options.setFilter("nearest")),
    l: $data.currentFilter === "rating" ? 1 : "",
    m: common_vendor.o(($event) => $options.setFilter("rating")),
    n: $data.currentFilter === "orders" ? 1 : "",
    o: common_vendor.o(($event) => $options.setFilter("orders")),
    p: common_vendor.f($data.courierList, (item, index, i0) => {
      return {
        a: item.avatar || "/static/images/default-avatar.png",
        b: common_vendor.t(item.name),
        c: "7933d590-3-" + i0,
        d: common_vendor.t(item.rating),
        e: common_vendor.t(item.completedOrders),
        f: common_vendor.t(item.serviceTime),
        g: "7933d590-4-" + i0,
        h: common_vendor.t(item.serviceArea),
        i: common_vendor.f(item.tags, (tag, tagIndex, i1) => {
          return {
            a: common_vendor.t(tag),
            b: tagIndex
          };
        }),
        j: common_vendor.t(item.distance),
        k: item.id,
        l: index,
        m: common_vendor.o(($event) => $options.navigateToDetail(item.id), index)
      };
    }),
    q: common_vendor.p({
      type: "star-filled",
      size: "14",
      color: "#ff9900"
    }),
    r: common_vendor.p({
      type: "location",
      size: "14",
      color: "#666"
    }),
    s: $data.hasMore
  }, $data.hasMore ? {} : {}, {
    t: common_vendor.o((...args) => $options.loadMore && $options.loadMore(...args)),
    v: common_vendor.o((...args) => $options.refresh && $options.refresh(...args)),
    w: $data.refreshing,
    x: $data.courierList.length === 0 && !$data.loading
  }, $data.courierList.length === 0 && !$data.loading ? {
    y: common_assets._imports_0$2
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
