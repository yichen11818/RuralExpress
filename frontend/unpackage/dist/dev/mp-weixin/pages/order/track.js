"use strict";
const common_vendor = require("../../common/vendor.js");
const uniIcons = () => "../../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
const _sfc_main = {
  components: {
    uniIcons
  },
  data() {
    return {
      trackingNo: "",
      logistics: {
        company: "顺丰速运",
        logo: "/static/images/sf-logo.png",
        trackingNo: "SF1234567890",
        status: 4,
        // 0-待揽收，1-已揽收，2-运输中，3-已到达，4-派送中，5-已签收
        statusText: "派送中",
        estimatedTime: "今天 18:00前",
        address: "江西省南昌市青山湖区艾溪湖北路77号",
        receiver: "张先生 (138****6677)",
        hasReviewed: false,
        courier: {
          name: "张师傅",
          phone: "135****8888",
          avatar: "/static/images/courier-avatar.png"
        },
        timeline: [
          {
            status: "派送中",
            time: "2023-03-21 09:45:32",
            detail: "【南昌市】您的快递正在派送中，快递员张师傅，电话135****8888"
          },
          {
            status: "已到达",
            time: "2023-03-21 07:30:15",
            detail: "【南昌市】快件已到达南昌青山湖区公司"
          },
          {
            status: "运输中",
            time: "2023-03-20 23:15:48",
            detail: "【赣州市】快件已从赣州发出，下一站南昌转运中心"
          },
          {
            status: "已揽收",
            time: "2023-03-20 17:22:10",
            detail: "【赣州市】顺丰快递员已揽收，将送往赣州转运中心"
          },
          {
            status: "待揽收",
            time: "2023-03-20 15:30:25",
            detail: "【赣州市】商家已发货，等待快递揽收"
          }
        ]
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
    } else {
      common_vendor.index.showToast({
        title: "缺少运单号",
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
      this.logistics.trackingNo = this.trackingNo;
    },
    // 刷新物流时间线
    refreshTimeline() {
      if (this.refreshing)
        return;
      this.refreshing = true;
      common_vendor.index.showLoading({
        title: "刷新中..."
      });
      setTimeout(() => {
        common_vendor.index.hideLoading();
        this.refreshing = false;
        common_vendor.index.showToast({
          title: "物流信息已更新",
          icon: "success"
        });
        common_vendor.index.stopPullDownRefresh();
      }, 1500);
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
