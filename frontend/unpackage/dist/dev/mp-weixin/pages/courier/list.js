"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
require("../../utils/request.js");
const api_search = require("../../api/search.js");
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
      courierList: [],
      latitude: null,
      longitude: null
    };
  },
  onLoad() {
    this.getCurrentLocation();
  },
  // 添加onShow生命周期函数，确保每次页面显示时都能加载数据
  onShow() {
    if (this.courierList.length === 0 && !this.loading) {
      this.loadCourierData();
    }
  },
  methods: {
    // 获取当前位置
    getCurrentLocation() {
      common_vendor.index.getLocation({
        type: "gcj02",
        success: (res) => {
          this.latitude = res.latitude;
          this.longitude = res.longitude;
          common_vendor.index.request({
            url: "https://apis.map.qq.com/ws/geocoder/v1/",
            data: {
              location: `${res.latitude},${res.longitude}`,
              key: utils_config.config.mapKey
              // 使用导入的config对象
            },
            success: (locationRes) => {
              if (locationRes.data.status === 0) {
                const result = locationRes.data.result;
                const addressComponent = result.address_component;
                this.selectedRegion = [
                  addressComponent.province,
                  addressComponent.city,
                  addressComponent.district
                ];
                this.loadCourierData();
              }
            },
            fail: () => {
              this.loadCourierData();
            }
          });
        },
        fail: () => {
          console.log("获取位置失败，使用赣州模拟位置");
          this.latitude = 25.831829;
          this.longitude = 114.935029;
          this.selectedRegion = ["江西省", "赣州市", "章贡区"];
          console.log("模拟位置信息：", {
            latitude: this.latitude,
            longitude: this.longitude,
            region: this.selectedRegion
          });
          this.loadCourierData();
        }
      });
    },
    // 加载快递员数据
    loadCourierData(append = false) {
      if (this.loading && !this.refreshing && append)
        return;
      this.loading = true;
      api_search.searchCouriers("", this.page, 10).then((res) => {
        if (res.code === 200) {
          const data = res.data;
          let list = data.list || [];
          if (this.selectedRegion && this.selectedRegion[0]) {
            list = list.filter((item) => {
              return (!this.selectedRegion[0] || item.province === this.selectedRegion[0]) && (!this.selectedRegion[1] || item.city === this.selectedRegion[1]) && (!this.selectedRegion[2] || item.district === this.selectedRegion[2]);
            });
          }
          if (this.currentFilter === "rating") {
            list.sort((a, b) => (b.rating || 0) - (a.rating || 0));
          } else if (this.currentFilter === "orders") {
            list.sort((a, b) => (b.completedOrders || 0) - (a.completedOrders || 0));
          }
          if (append) {
            this.courierList = [...this.courierList, ...list];
          } else {
            this.courierList = list;
          }
          this.hasMore = data.page < data.totalPage;
        } else {
          common_vendor.index.showToast({
            title: res.message || "获取快递员列表失败",
            icon: "none"
          });
        }
      }).catch(() => {
        common_vendor.index.showToast({
          title: "网络错误，请稍后重试",
          icon: "none"
        });
      }).finally(() => {
        this.loading = false;
        if (this.refreshing) {
          this.refreshing = false;
          common_vendor.index.stopPullDownRefresh();
        }
      });
    },
    // 地区选择变化
    regionChange(e) {
      this.selectedRegion = e.detail.value;
      this.page = 1;
      this.courierList = [];
      this.hasMore = true;
      this.loadCourierData();
    },
    // 设置筛选条件
    setFilter(filter) {
      if (this.currentFilter === filter)
        return;
      this.currentFilter = filter;
      this.page = 1;
      this.courierList = [];
      this.hasMore = true;
      this.loadCourierData();
    },
    // 刷新列表
    refresh() {
      this.refreshing = true;
      this.page = 1;
      this.loadCourierData();
    },
    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading)
        return;
      this.page++;
      this.loadCourierData(true);
    },
    // 跳转到快递员详情页
    navigateToDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/courier/detail?id=${id}`
      });
    },
    // 联系快递员
    contactCourier(e) {
      const id = e.currentTarget.dataset.id;
      const courier = this.courierList.find((item) => item.id === id);
      if (courier && courier.phone) {
        common_vendor.index.makePhoneCall({
          phoneNumber: courier.phone
        });
      } else {
        common_vendor.index.showToast({
          title: "暂无联系方式",
          icon: "none"
        });
      }
    },
    // 显示搜索页面
    showSearch() {
      common_vendor.index.navigateTo({
        url: "/pages/search/search?type=courier"
      });
    },
    // 添加重新加载方法
    retryLoad() {
      this.page = 1;
      common_vendor.index.showLoading({
        title: "加载中"
      });
      this.loadCourierData();
      setTimeout(() => {
        common_vendor.index.hideLoading();
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
    p: $data.courierList.length === 0 && $data.loading
  }, $data.courierList.length === 0 && $data.loading ? {} : {}, {
    q: common_vendor.f($data.courierList, (item, index, i0) => {
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
    r: common_vendor.p({
      type: "star-filled",
      size: "14",
      color: "#ff9900"
    }),
    s: common_vendor.p({
      type: "location",
      size: "14",
      color: "#666"
    }),
    t: $data.hasMore
  }, $data.hasMore ? {} : {}, {
    v: common_vendor.o((...args) => $options.loadMore && $options.loadMore(...args)),
    w: common_vendor.o((...args) => $options.refresh && $options.refresh(...args)),
    x: $data.refreshing,
    y: $data.courierList.length === 0 && !$data.loading
  }, $data.courierList.length === 0 && !$data.loading ? {
    z: common_assets._imports_0$2,
    A: common_vendor.o((...args) => $options.retryLoad && $options.retryLoad(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
