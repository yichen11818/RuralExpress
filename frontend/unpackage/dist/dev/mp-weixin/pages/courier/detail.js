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
      courierId: null,
      reviewFilter: "全部评价",
      reviewPage: 1,
      hasMoreReviews: true,
      courierInfo: {
        id: 1,
        name: "张师傅",
        avatar: "/static/images/courier1.jpg",
        rating: 4.9,
        ratingCount: 326,
        completedOrders: 526,
        serviceTime: 8,
        responseTime: 15,
        serviceStatus: 1,
        // 0-休息中，1-接单中
        serviceArea: "江西省南昌市青山湖区湖坊镇、艾溪湖区域",
        workStartTime: "08:00",
        workEndTime: "20:00",
        vehicle: "电动三轮车",
        introduction: "您好，我是张师傅，本地人，熟悉社区环境。送件准时，服务热情，欢迎下单！",
        tags: ["准时送达", "服务态度好", "有礼貌", "送货快", "认真负责"]
      },
      reviews: [
        {
          id: 1,
          userName: "客户152****8899",
          userAvatar: "/static/images/default-avatar.png",
          rating: 5,
          content: "送件非常及时，态度很好，下雨天还能及时送达，给五星好评！",
          time: "2023-03-18",
          orderInfo: "文件快递 3公里 2023-03-18 送达",
          reply: "感谢您的认可，我们会继续努力提供更好的服务！"
        },
        {
          id: 2,
          userName: "客户138****6677",
          userAvatar: "/static/images/default-avatar.png",
          rating: 5,
          content: "快递员很专业，打包很仔细，而且提前跟我确认了送达时间，非常满意。",
          time: "2023-03-15",
          orderInfo: "小件快递 5公里 2023-03-15 送达"
        },
        {
          id: 3,
          userName: "客户186****3344",
          userAvatar: "/static/images/default-avatar.png",
          rating: 4,
          content: "送货速度挺快的，但是没有提前打电话通知我，其他都挺好的。",
          time: "2023-03-10",
          orderInfo: "中件快递 2公里 2023-03-10 送达",
          reply: "非常抱歉给您带来不便，我们会改进服务流程，下次一定提前联系您！"
        }
      ]
    };
  },
  onLoad(options) {
    if (options.id) {
      this.courierId = options.id;
    }
  },
  methods: {
    // 请求快递员信息
    getCourierInfo(id) {
    },
    // 请求评价列表
    getReviews() {
    },
    // 加载更多评价
    loadMoreReviews() {
      this.reviewPage++;
      this.getReviews();
    },
    // 显示筛选选项
    showFilterOptions() {
      common_vendor.index.showActionSheet({
        itemList: ["全部评价", "好评", "中评", "差评"],
        success: (res) => {
          const filters = ["全部评价", "好评", "中评", "差评"];
          this.reviewFilter = filters[res.tapIndex];
          this.reviewPage = 1;
          this.getReviews();
        }
      });
    },
    // 联系快递员
    callCourier() {
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
    },
    // 创建订单
    createOrder() {
      common_vendor.index.navigateTo({
        url: `/pages/delivery/send?courierId=${this.courierId}`
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
    a: $data.courierInfo.avatar || "/static/images/default-avatar.png",
    b: common_vendor.t($data.courierInfo.name),
    c: $data.courierInfo.serviceStatus === 1
  }, $data.courierInfo.serviceStatus === 1 ? {} : {}, {
    d: common_vendor.p({
      type: "star-filled",
      size: "14",
      color: "#ff9900"
    }),
    e: common_vendor.t($data.courierInfo.rating),
    f: common_vendor.t($data.courierInfo.ratingCount),
    g: common_vendor.t($data.courierInfo.completedOrders),
    h: common_vendor.t($data.courierInfo.serviceTime),
    i: common_vendor.t($data.courierInfo.responseTime),
    j: common_vendor.p({
      type: "phone-filled",
      size: "20",
      color: "#fff"
    }),
    k: common_vendor.o((...args) => $options.callCourier && $options.callCourier(...args)),
    l: common_vendor.p({
      type: "plusempty",
      size: "20",
      color: "#fff"
    }),
    m: common_vendor.o((...args) => $options.createOrder && $options.createOrder(...args)),
    n: common_vendor.t($data.courierInfo.serviceArea),
    o: common_vendor.t($data.courierInfo.serviceTime ? "每天 " + $data.courierInfo.workStartTime + " - " + $data.courierInfo.workEndTime : "暂无"),
    p: common_vendor.t($data.courierInfo.vehicle),
    q: common_vendor.t($data.courierInfo.introduction || "这位快递员很懒，还没有填写简介~"),
    r: common_vendor.f($data.courierInfo.tags, (tag, index, i0) => {
      return {
        a: common_vendor.t(tag),
        b: index
      };
    }),
    s: common_vendor.t($data.reviewFilter),
    t: common_vendor.p({
      type: "arrowdown",
      size: "14",
      color: "#666"
    }),
    v: common_vendor.o((...args) => $options.showFilterOptions && $options.showFilterOptions(...args)),
    w: $data.reviews.length > 0
  }, $data.reviews.length > 0 ? {
    x: common_vendor.f($data.reviews, (item, index, i0) => {
      return common_vendor.e({
        a: item.userAvatar,
        b: common_vendor.t(item.userName),
        c: common_vendor.t(item.time),
        d: common_vendor.f(5, (i, k1, i1) => {
          return {
            a: i,
            b: "31f9857a-4-" + i0 + "-" + i1,
            c: common_vendor.p({
              type: i <= item.rating ? "star-filled" : "star",
              size: "14",
              color: i <= item.rating ? "#ff9900" : "#ddd"
            })
          };
        }),
        e: common_vendor.t(item.content),
        f: item.orderInfo
      }, item.orderInfo ? {
        g: common_vendor.t(item.orderInfo)
      } : {}, {
        h: item.reply
      }, item.reply ? {
        i: common_vendor.t(item.reply)
      } : {}, {
        j: index
      });
    })
  } : {
    y: common_assets._imports_0$2
  }, {
    z: $data.reviews.length > 0 && $data.hasMoreReviews
  }, $data.reviews.length > 0 && $data.hasMoreReviews ? {
    A: common_vendor.o((...args) => $options.loadMoreReviews && $options.loadMoreReviews(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
