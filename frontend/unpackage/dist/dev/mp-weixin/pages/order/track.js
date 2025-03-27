"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const uniIcons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const _sfc_main = {
  components: {
    uniIcons
  },
  data() {
    return {
      trackingNo: "",
      orderId: null,
      loading: false,
      logistics: {
        company: "",
        logo: "/static/images/package.png",
        trackingNo: "",
        status: 0,
        statusText: "等待揽收",
        estimatedTime: "",
        address: "",
        receiver: "",
        hasReviewed: false,
        courier: null,
        timeline: []
      },
      notifications: {
        statusUpdate: true,
        estimatedArrival: true,
        signed: true
      },
      refreshing: false
    };
  },
  onLoad(options) {
    if (options.trackingNo) {
      this.trackingNo = options.trackingNo;
      this.loadTrackingInfo();
    } else if (options.orderId) {
      this.orderId = options.orderId;
      this.loadTrackingInfo();
    } else {
      common_vendor.index.showToast({
        title: "缺少运单号或订单ID",
        icon: "none"
      });
      setTimeout(() => {
        common_vendor.index.navigateBack();
      }, 1500);
    }
  },
  onPullDownRefresh() {
    this.refreshTimeline();
  },
  methods: {
    // 加载物流信息
    loadTrackingInfo() {
      if (this.loading)
        return;
      this.loading = true;
      common_vendor.index.showLoading({
        title: "加载中..."
      });
      const params = {};
      if (this.trackingNo) {
        params.trackingNo = this.trackingNo;
      } else if (this.orderId) {
        params.orderId = this.orderId;
      }
      api_order.getLogisticsInfo(params).then((res) => {
        if (res.code === 200 && res.data) {
          const logisticsData = res.data;
          this.logistics = {
            company: logisticsData.companyName || "未知快递公司",
            logo: logisticsData.companyLogo || "/static/images/package.png",
            trackingNo: logisticsData.trackingNo || this.trackingNo,
            status: logisticsData.status || 0,
            statusText: this.getStatusTextByCode(logisticsData.status),
            estimatedTime: logisticsData.estimatedTime || "暂无信息",
            address: logisticsData.address || "暂无信息",
            receiver: logisticsData.receiver || "暂无信息",
            hasReviewed: logisticsData.hasReviewed || false,
            courier: logisticsData.courier || null,
            timeline: logisticsData.timeline || []
          };
          if (this.trackingNo === "") {
            this.trackingNo = this.logistics.trackingNo;
          }
        } else {
          common_vendor.index.showToast({
            title: res.message || "获取物流信息失败",
            icon: "none"
          });
        }
      }).catch((err) => {
        console.error("获取物流信息失败", err);
        common_vendor.index.showToast({
          title: "获取物流信息失败",
          icon: "none"
        });
      }).finally(() => {
        common_vendor.index.hideLoading();
        this.loading = false;
      });
    },
    // 获取物流状态文本
    getStatusTextByCode(status) {
      const statusMap = {
        0: "等待揽收",
        1: "已揽收",
        2: "运输中",
        3: "已到达",
        4: "派送中",
        5: "已签收"
      };
      return statusMap[status] || "未知状态";
    },
    // 刷新物流时间线
    refreshTimeline() {
      if (this.refreshing)
        return;
      this.refreshing = true;
      common_vendor.index.showLoading({
        title: "刷新中..."
      });
      this.loadTrackingInfo();
      setTimeout(() => {
        this.refreshing = false;
        common_vendor.index.stopPullDownRefresh();
      }, 500);
    },
    // 获取物流状态样式类
    getStatusClass() {
      const statusMap = {
        0: "status-waiting",
        1: "status-collected",
        2: "status-transporting",
        3: "status-arrived",
        4: "status-delivering",
        5: "status-signed"
      };
      return statusMap[this.logistics.status] || "";
    },
    // 复制运单号
    copyTrackingNo() {
      common_vendor.index.setClipboardData({
        data: this.logistics.trackingNo,
        success: () => {
          common_vendor.index.showToast({
            title: "复制成功",
            icon: "success"
          });
        }
      });
    },
    // 分享物流信息
    shareTracking() {
      common_vendor.index.share({
        provider: "weixin",
        scene: "WXSceneSession",
        type: 0,
        title: `${this.logistics.company}物流轨迹`,
        summary: `运单号：${this.logistics.trackingNo}，当前状态：${this.logistics.statusText}`,
        imageUrl: "/static/images/package.png",
        success: (res) => {
          console.log("分享成功", res);
        },
        fail: (err) => {
          console.log("分享失败", err);
        }
      });
    },
    // 拨打电话联系快递员
    callCourier(phone) {
      if (!phone)
        return;
      const realPhone = phone.replace(/\*+/g, "");
      if (realPhone.length < 11) {
        common_vendor.index.showToast({
          title: "暂无完整电话号码",
          icon: "none"
        });
        return;
      }
      common_vendor.index.makePhoneCall({
        phoneNumber: realPhone,
        fail: () => {
          common_vendor.index.showToast({
            title: "拨打电话失败",
            icon: "none"
          });
        }
      });
    },
    // 评价订单
    reviewOrder() {
      common_vendor.index.navigateTo({
        url: `/pages/order/review?id=${this.logistics.orderId}`
      });
    },
    // 切换通知设置
    toggleNotification(type) {
      this.notifications[type] = !this.notifications[type];
      this.saveNotificationSettings();
    },
    // 保存通知设置
    saveNotificationSettings() {
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
    a: $data.logistics.logo || "/static/images/package.png",
    b: common_vendor.t($data.logistics.company),
    c: common_vendor.t($data.logistics.trackingNo),
    d: common_vendor.t($data.logistics.statusText),
    e: common_vendor.n($options.getStatusClass()),
    f: $data.logistics.courier
  }, $data.logistics.courier ? {
    g: $data.logistics.courier.avatar || "/static/images/user.png",
    h: common_vendor.t($data.logistics.courier.name),
    i: common_vendor.t($data.logistics.courier.phone),
    j: common_vendor.o(($event) => $options.callCourier($data.logistics.courier.phone))
  } : {}, {
    k: common_vendor.t($data.logistics.estimatedTime || "暂无信息"),
    l: common_vendor.t($data.logistics.address || "暂无信息"),
    m: common_vendor.t($data.logistics.receiver || "暂无信息"),
    n: common_vendor.o((...args) => $options.refreshTimeline && $options.refreshTimeline(...args)),
    o: common_vendor.f($data.logistics.timeline, (item, index, i0) => {
      return common_vendor.e({
        a: index === 0 ? 1 : "",
        b: common_vendor.t(item.status),
        c: common_vendor.t(item.time),
        d: item.detail
      }, item.detail ? {
        e: common_vendor.t(item.detail)
      } : {}, {
        f: index,
        g: index === 0 ? 1 : ""
      });
    }),
    p: $data.logistics.timeline.length === 0
  }, $data.logistics.timeline.length === 0 ? {} : {}, {
    q: $data.logistics.status !== 5
  }, $data.logistics.status !== 5 ? {
    r: common_vendor.p({
      type: "paperclip",
      size: "18",
      color: "#666"
    }),
    s: common_vendor.o((...args) => $options.copyTrackingNo && $options.copyTrackingNo(...args)),
    t: common_vendor.p({
      type: "redo",
      size: "18",
      color: "#fff"
    }),
    v: common_vendor.o((...args) => $options.shareTracking && $options.shareTracking(...args))
  } : {}, {
    w: $data.logistics.status === 5 && !$data.logistics.hasReviewed
  }, $data.logistics.status === 5 && !$data.logistics.hasReviewed ? {
    x: common_vendor.p({
      type: "star",
      size: "18",
      color: "#fff"
    }),
    y: common_vendor.o((...args) => $options.reviewOrder && $options.reviewOrder(...args))
  } : {}, {
    z: $data.notifications.statusUpdate,
    A: common_vendor.o(($event) => $options.toggleNotification("statusUpdate")),
    B: $data.notifications.estimatedArrival,
    C: common_vendor.o(($event) => $options.toggleNotification("estimatedArrival")),
    D: $data.notifications.signed,
    E: common_vendor.o(($event) => $options.toggleNotification("signed"))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
