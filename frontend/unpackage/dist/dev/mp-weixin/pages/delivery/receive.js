"use strict";
const common_vendor = require("../../common/vendor.js");
const api_auth = require("../../api/auth.js");
const common_assets = require("../../common/assets.js");
const uniIcons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const uniPopup = () => "../../uni_modules/uni-popup/components/uni-popup/uni-popup.js";
const _sfc_main = {
  components: {
    uniIcons,
    uniPopup
  },
  data() {
    return {
      searchText: "",
      currentTab: 0,
      page: 1,
      hasMore: true,
      queryResult: null,
      pendingPackages: [
        {
          id: 1,
          company: "顺丰速运",
          trackingNo: "SF1234567890",
          status: 3,
          // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
          statusText: "已到达",
          address: "江西省南昌市青山湖区艾溪湖北路77号",
          phone: "138****6677",
          estimatedTime: "今天 18:00前",
          courierId: 1,
          courierName: "张师傅"
        },
        {
          id: 2,
          company: "中通快递",
          trackingNo: "ZT9876543210",
          status: 4,
          // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
          statusText: "派送中",
          address: "江西省南昌市青山湖区高新开发区创新一路",
          phone: "152****8899",
          estimatedTime: "今天 16:00前",
          courierId: 2,
          courierName: "李师傅"
        }
      ],
      receivedPackages: [
        {
          id: 3,
          company: "圆通速递",
          trackingNo: "YT5678901234",
          status: 5,
          // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
          statusText: "已签收",
          address: "江西省南昌市青山湖区高新开发区创新一路",
          phone: "152****8899",
          receivedTime: "2023-03-18 15:23",
          courierId: 3,
          courierName: "王师傅",
          hasReviewed: true
        },
        {
          id: 4,
          company: "京东物流",
          trackingNo: "JD6789012345",
          status: 5,
          // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
          statusText: "已签收",
          address: "江西省南昌市青山湖区艾溪湖北路77号",
          phone: "138****6677",
          receivedTime: "2023-03-15 10:45",
          courierId: 1,
          courierName: "张师傅",
          hasReviewed: false
        }
      ]
    };
  },
  onLoad() {
    if (!api_auth.isLoggedIn()) {
      common_vendor.index.showModal({
        title: "提示",
        content: "请先登录后再查询包裹",
        showCancel: false,
        success: () => {
          common_vendor.index.navigateTo({
            url: "/pages/login/login"
          });
        }
      });
      return;
    }
  },
  onPullDownRefresh() {
    this.page = 1;
    setTimeout(() => {
      common_vendor.index.stopPullDownRefresh();
    }, 1e3);
  },
  methods: {
    // 加载包裹列表
    loadPackages() {
    },
    // 切换标签页
    switchTab(index) {
      if (this.currentTab === index)
        return;
      this.currentTab = index;
      this.page = 1;
    },
    // 扫描二维码
    scanCode() {
      common_vendor.index.scanCode({
        scanType: ["qrCode", "barCode"],
        success: (res) => {
          this.searchText = res.result;
          this.searchPackage();
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "扫码失败，请重试",
            icon: "none"
          });
        }
      });
    },
    // 搜索包裹
    searchPackage() {
      if (!this.searchText) {
        return common_vendor.index.showToast({
          title: "请输入或扫描快递单号",
          icon: "none"
        });
      }
      common_vendor.index.showLoading({
        title: "正在查询..."
      });
      setTimeout(() => {
        common_vendor.index.hideLoading();
        if (this.searchText.length >= 8) {
          this.queryResult = {
            company: "顺丰速运",
            trackingNo: this.searchText,
            status: 3,
            statusText: "已到达",
            receiverName: "张三",
            receiverPhone: "138****6677",
            address: "江西省南昌市青山湖区艾溪湖北路77号",
            estimatedTime: "今天 18:00前"
          };
        } else {
          this.queryResult = null;
        }
        this.$refs.queryPopup.open();
      }, 1500);
    },
    // 关闭弹窗
    closePopup() {
      this.$refs.queryPopup.close();
    },
    // 添加到我的包裹
    addToMyPackage() {
      common_vendor.index.showToast({
        title: "添加成功",
        icon: "success"
      });
      this.closePopup();
      const index = this.pendingPackages.findIndex((item) => item.id === id);
      if (index !== -1) {
        const packageItem = this.pendingPackages[index];
        packageItem.status = 5;
        packageItem.statusText = "已签收";
        packageItem.receivedTime = this.formatDate(/* @__PURE__ */ new Date());
        packageItem.hasReviewed = false;
        this.receivedPackages.unshift(packageItem);
        this.pendingPackages.splice(index, 1);
      }
    },
    // 确认收件
    receivePackage(id2) {
      common_vendor.index.showModal({
        title: "确认收件",
        content: "确认已收到该包裹？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.showToast({
              title: "收件成功",
              icon: "success"
            });
            const index = this.pendingPackages.findIndex((item) => item.id === id2);
            if (index !== -1) {
              const packageItem = this.pendingPackages[index];
              packageItem.status = 5;
              packageItem.statusText = "已签收";
              packageItem.receivedTime = this.formatDate(/* @__PURE__ */ new Date());
              packageItem.hasReviewed = false;
              this.receivedPackages.unshift(packageItem);
              this.pendingPackages.splice(index, 1);
            }
          }
        }
      });
    },
    // 追踪包裹
    trackPackage(trackingNo) {
      common_vendor.index.navigateTo({
        url: `/pages/order/track?trackingNo=${trackingNo}`
      });
    },
    // 联系快递员
    contactCourier(courierId) {
      common_vendor.index.navigateTo({
        url: `/pages/courier/detail?id=${courierId}`
      });
    },
    // 评价订单
    reviewOrder(id2) {
      common_vendor.index.navigateTo({
        url: `/pages/order/review?id=${id2}`
      });
    },
    // 复制单号
    copyTrackingNo(trackingNo) {
      common_vendor.index.setClipboardData({
        data: trackingNo,
        success: () => {
          common_vendor.index.showToast({
            title: "复制成功",
            icon: "success"
          });
        }
      });
    },
    // 加载更多
    loadMore() {
      if (!this.hasMore)
        return;
      this.page++;
    },
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, "0");
      const day = date.getDate().toString().padStart(2, "0");
      const hour = date.getHours().toString().padStart(2, "0");
      const minute = date.getMinutes().toString().padStart(2, "0");
      return `${year}-${month}-${day} ${hour}:${minute}`;
    }
  }
};
if (!Array) {
  const _easycom_uni_icons2 = common_vendor.resolveComponent("uni-icons");
  const _easycom_uni_popup2 = common_vendor.resolveComponent("uni-popup");
  (_easycom_uni_icons2 + _easycom_uni_popup2)();
}
const _easycom_uni_icons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const _easycom_uni_popup = () => "../../uni_modules/uni-popup/components/uni-popup/uni-popup.js";
if (!Math) {
  (_easycom_uni_icons + _easycom_uni_popup)();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.p({
      type: "search",
      size: "18",
      color: "#666"
    }),
    b: common_vendor.o((...args) => $options.searchPackage && $options.searchPackage(...args)),
    c: $data.searchText,
    d: common_vendor.o(($event) => $data.searchText = $event.detail.value),
    e: common_vendor.o((...args) => $options.searchPackage && $options.searchPackage(...args)),
    f: common_vendor.p({
      type: "scan",
      size: "36",
      color: "#3cc51f"
    }),
    g: common_vendor.o((...args) => $options.scanCode && $options.scanCode(...args)),
    h: $data.currentTab === 0 ? 1 : "",
    i: common_vendor.o(($event) => $options.switchTab(0)),
    j: $data.currentTab === 1 ? 1 : "",
    k: common_vendor.o(($event) => $options.switchTab(1)),
    l: $data.currentTab === 0
  }, $data.currentTab === 0 ? common_vendor.e({
    m: $data.pendingPackages.length === 0
  }, $data.pendingPackages.length === 0 ? {
    n: common_assets._imports_0$2
  } : {}, {
    o: common_vendor.f($data.pendingPackages, (item, index, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.company),
        b: common_vendor.t(item.statusText),
        c: common_vendor.t(item.trackingNo),
        d: common_vendor.o(($event) => $options.copyTrackingNo(item.trackingNo), index),
        e: common_vendor.t(item.address),
        f: common_vendor.t(item.phone),
        g: common_vendor.t(item.estimatedTime),
        h: "760f13d0-2-" + i0,
        i: common_vendor.o(($event) => $options.trackPackage(item.trackingNo), index),
        j: item.status === 4
      }, item.status === 4 ? {
        k: "760f13d0-3-" + i0,
        l: common_vendor.p({
          type: "checkmarkempty",
          size: "16",
          color: "#fff"
        }),
        m: common_vendor.o(($event) => $options.receivePackage(item.id), index)
      } : {
        n: "760f13d0-4-" + i0,
        o: common_vendor.p({
          type: "phone-filled",
          size: "16",
          color: "#fff"
        }),
        p: common_vendor.o(($event) => $options.contactCourier(item.courierId), index)
      }, {
        q: index
      });
    }),
    p: common_vendor.p({
      type: "location",
      size: "16",
      color: "#3cc51f"
    })
  }) : {}, {
    q: $data.currentTab === 1
  }, $data.currentTab === 1 ? common_vendor.e({
    r: $data.receivedPackages.length === 0
  }, $data.receivedPackages.length === 0 ? {
    s: common_assets._imports_0$2
  } : {}, {
    t: common_vendor.f($data.receivedPackages, (item, index, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.company),
        b: common_vendor.t(item.statusText),
        c: common_vendor.t(item.trackingNo),
        d: common_vendor.o(($event) => $options.copyTrackingNo(item.trackingNo), index),
        e: common_vendor.t(item.address),
        f: common_vendor.t(item.receivedTime),
        g: common_vendor.t(item.courierName),
        h: "760f13d0-5-" + i0,
        i: common_vendor.o(($event) => $options.trackPackage(item.trackingNo), index),
        j: !item.hasReviewed
      }, !item.hasReviewed ? {
        k: "760f13d0-6-" + i0,
        l: common_vendor.p({
          type: "star",
          size: "16",
          color: "#fff"
        }),
        m: common_vendor.o(($event) => $options.reviewOrder(item.id), index)
      } : {
        n: "760f13d0-7-" + i0,
        o: common_vendor.p({
          type: "star-filled",
          size: "16",
          color: "#fff"
        })
      }, {
        p: index
      });
    }),
    v: common_vendor.p({
      type: "location",
      size: "16",
      color: "#3cc51f"
    }),
    w: $data.receivedPackages.length > 0 && $data.hasMore
  }, $data.receivedPackages.length > 0 && $data.hasMore ? {
    x: common_vendor.o((...args) => $options.loadMore && $options.loadMore(...args))
  } : {}, {
    y: $data.receivedPackages.length > 0 && !$data.hasMore
  }, $data.receivedPackages.length > 0 && !$data.hasMore ? {} : {}) : {}, {
    z: common_vendor.o($options.closePopup),
    A: common_vendor.p({
      type: "close",
      size: "20",
      color: "#999"
    }),
    B: $data.queryResult
  }, $data.queryResult ? {
    C: common_vendor.t($data.queryResult.company),
    D: common_vendor.t($data.queryResult.trackingNo),
    E: common_vendor.t($data.queryResult.statusText),
    F: common_vendor.t($data.queryResult.receiverName),
    G: common_vendor.t($data.queryResult.receiverPhone),
    H: common_vendor.t($data.queryResult.address),
    I: common_vendor.t($data.queryResult.estimatedTime)
  } : {
    J: common_vendor.p({
      type: "info",
      size: "64",
      color: "#999"
    })
  }, {
    K: common_vendor.o((...args) => $options.closePopup && $options.closePopup(...args)),
    L: $data.queryResult
  }, $data.queryResult ? {
    M: common_vendor.o((...args) => $options.addToMyPackage && $options.addToMyPackage(...args))
  } : {}, {
    N: common_vendor.sr("queryPopup", "760f13d0-8"),
    O: common_vendor.p({
      type: "center"
    })
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
