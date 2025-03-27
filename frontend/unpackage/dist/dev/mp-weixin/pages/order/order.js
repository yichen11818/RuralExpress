"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const api_order = require("../../api/order.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      // 标签页
      tabs: [
        { name: "全部" },
        { name: "待接单" },
        { name: "处理中" },
        { name: "已完成" }
      ],
      // 当前标签页
      currentTab: 0,
      // 订单列表
      orderList: [],
      // 用户信息
      userInfo: null,
      // 分页信息
      page: 1,
      size: 10,
      total: 0,
      // 加载状态
      loading: false
    };
  },
  onLoad() {
    if (!api_auth.isLoggedIn()) {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
      return;
    }
    this.userInfo = api_auth.getUserInfo();
    this.loadOrderData();
  },
  onPullDownRefresh() {
    this.page = 1;
    this.loadOrderData();
    setTimeout(() => {
      common_vendor.index.stopPullDownRefresh();
    }, 1e3);
  },
  methods: {
    // 切换标签页
    switchTab(index) {
      if (this.currentTab === index)
        return;
      this.currentTab = index;
      this.page = 1;
      this.orderList = [];
      this.loadOrderData();
    },
    // 加载订单数据
    loadOrderData() {
      if (!this.userInfo || !this.userInfo.id) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
        return;
      }
      this.loading = true;
      const params = {
        page: this.page,
        size: this.size
      };
      switch (this.currentTab) {
        case 1:
          params.status = 0;
          break;
        case 2:
          this.loadProcessingOrders();
          return;
        case 3:
          params.status = 6;
          break;
      }
      api_order.getUserOrders(this.userInfo.id, params).then((res) => {
        console.log("获取订单响应:", res);
        if (res.code === 200 && res.data) {
          if (this.page === 1) {
            this.orderList = res.data.records || [];
          } else {
            this.orderList = [...this.orderList, ...res.data.records || []];
          }
          this.total = res.data.total || 0;
        } else {
          common_vendor.index.showToast({
            title: res.message || "获取订单失败",
            icon: "none"
          });
        }
      }).catch((err) => {
        console.error("获取订单失败", err);
        common_vendor.index.showToast({
          title: "获取订单失败",
          icon: "none"
        });
      }).finally(() => {
        this.loading = false;
      });
    },
    // 加载处理中的订单（包含多种状态）
    loadProcessingOrders() {
      if (!this.userInfo || !this.userInfo.id)
        return;
      const promises = [];
      for (let status = 1; status <= 4; status++) {
        const params = {
          page: this.page,
          size: this.size,
          status
        };
        promises.push(api_order.getUserOrders(this.userInfo.id, params));
      }
      Promise.all(promises).then((results) => {
        let processingOrders = [];
        results.forEach((res) => {
          if (res.code === 200 && res.data && res.data.records) {
            processingOrders = [...processingOrders, ...res.data.records];
          }
        });
        processingOrders.sort((a, b) => {
          return new Date(b.createdAt) - new Date(a.createdAt);
        });
        if (this.page === 1) {
          this.orderList = processingOrders;
        } else {
          this.orderList = [...this.orderList, ...processingOrders];
        }
      }).catch((err) => {
        console.error("获取处理中订单失败", err);
        common_vendor.index.showToast({
          title: "获取订单失败",
          icon: "none"
        });
      }).finally(() => {
        this.loading = false;
      });
    },
    // 获取状态文本
    getStatusText(status) {
      return api_order.getOrderStatusText(status);
    },
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone)
        return "";
      return phone.substring(0, 3) + "****" + phone.substring(7);
    },
    // 格式化日期
    formatDate(dateStr) {
      return dateStr;
    },
    // 跳转到详情页
    navigateToDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/order/detail?id=${id}`
      });
    },
    // 取消订单
    cancelOrder(id) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要取消该订单吗？",
        success: (res) => {
          if (res.confirm) {
            api_order.cancelOrder(id, "用户主动取消").then((res2) => {
              if (res2.code === 200) {
                common_vendor.index.showToast({
                  title: "取消成功",
                  icon: "success"
                });
                this.loadOrderData();
              } else {
                common_vendor.index.showToast({
                  title: res2.message || "取消失败",
                  icon: "none"
                });
              }
            }).catch((err) => {
              console.error("取消订单失败", err);
              common_vendor.index.showToast({
                title: "取消失败",
                icon: "none"
              });
            });
          }
        }
      });
    },
    // 评价订单
    evaluateOrder(id) {
      common_vendor.index.navigateTo({
        url: `/pages/order/evaluate?id=${id}`
      });
    },
    // 导航到指定页面
    navigateTo(url) {
      common_vendor.index.switchTab({
        url
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.f($data.tabs, (item, index, i0) => {
      return {
        a: common_vendor.t(item.name),
        b: index,
        c: $data.currentTab === index ? 1 : "",
        d: common_vendor.o(($event) => $options.switchTab(index), index)
      };
    }),
    b: $data.orderList.length > 0
  }, $data.orderList.length > 0 ? {
    c: common_vendor.f($data.orderList, (item, index, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.orderNo),
        b: common_vendor.t($options.getStatusText(item.orderStatus)),
        c: common_vendor.n("status-" + item.orderStatus),
        d: common_vendor.t(item.senderName),
        e: common_vendor.t($options.formatPhone(item.senderPhone)),
        f: common_vendor.t(item.senderAddress),
        g: common_vendor.t(item.receiverName),
        h: common_vendor.t($options.formatPhone(item.receiverPhone)),
        i: common_vendor.t(item.receiverAddress),
        j: common_vendor.t($options.formatDate(item.createdAt)),
        k: item.orderStatus === 0
      }, item.orderStatus === 0 ? {
        l: common_vendor.o(($event) => $options.cancelOrder(item.id), index)
      } : {}, {
        m: item.orderStatus === 5
      }, item.orderStatus === 5 ? {
        n: common_vendor.o(($event) => $options.evaluateOrder(item.id), index)
      } : {}, {
        o: item.orderStatus === 5
      }, item.orderStatus === 5 ? {
        p: common_vendor.o(($event) => $options.navigateToDetail(item.id), index)
      } : {}, {
        q: index,
        r: common_vendor.o(($event) => $options.navigateToDetail(item.id), index)
      });
    })
  } : {
    d: common_assets._imports_0$2,
    e: common_vendor.o(($event) => $options.navigateTo("/pages/index/index"))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
