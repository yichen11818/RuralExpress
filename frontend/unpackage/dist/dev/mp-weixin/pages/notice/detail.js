"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      id: "",
      noticeData: {
        id: "",
        title: "",
        content: "",
        createTime: "",
        source: "乡递通",
        viewCount: 0
      },
      relatedNotices: []
    };
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id;
      this.getNoticeDetail();
      this.getRelatedNotices();
    }
  },
  methods: {
    // 获取公告详情
    getNoticeDetail() {
      common_vendor.index.showLoading({
        title: "加载中"
      });
      setTimeout(() => {
        this.noticeData = {
          id: this.id,
          title: "重要通知：乡递通服务升级公告",
          content: '<div style="line-height: 1.8;">亲爱的乡递通用户：<br/><br/>为了提供更好的服务体验，我们将于2023年12月15日凌晨2:00-6:00进行系统升级维护。在此期间，您可能无法正常使用乡递通的部分功能。<br/><br/>此次升级将带来以下改进：<br/><ol><li>优化寄件流程，提高下单效率</li><li>增强物流追踪功能，物流信息更加透明</li><li>改进用户界面，带来更直观的操作体验</li><li>提升系统安全性，更好地保护用户信息</li></ol><br/>感谢您对乡递通的支持与理解！如有疑问，请联系客服热线：400-123-4567。<br/><br/>乡递通团队<br/>2023年12月10日</div>',
          createTime: "2023-12-10 10:00:00",
          source: "乡递通官方",
          viewCount: 1243
        };
        common_vendor.index.hideLoading();
      }, 500);
    },
    // 获取相关公告
    getRelatedNotices() {
      setTimeout(() => {
        this.relatedNotices = [
          {
            id: "2",
            title: "乡递通春节期间配送调整通知",
            createTime: "2024-01-15 14:30:00"
          },
          {
            id: "3",
            title: "乡递通App新功能上线公告",
            createTime: "2023-11-20 09:15:00"
          },
          {
            id: "4",
            title: "乡递通配送范围扩展通知",
            createTime: "2023-10-05 16:45:00"
          }
        ];
      }, 800);
    },
    // 格式化日期
    formatDate(dateString) {
      if (!dateString)
        return "";
      const date = new Date(dateString.replace(/-/g, "/"));
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
    },
    // 跳转到另一个公告详情
    goToNoticeDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/notice/detail?id=${id}`
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.t($data.noticeData.title),
    b: common_vendor.t($options.formatDate($data.noticeData.createTime)),
    c: common_vendor.t($data.noticeData.source || "乡递通"),
    d: $data.noticeData.content,
    e: $data.relatedNotices.length > 0
  }, $data.relatedNotices.length > 0 ? {
    f: common_vendor.f($data.relatedNotices, (item, index, i0) => {
      return {
        a: common_vendor.t(item.title),
        b: common_vendor.t($options.formatDate(item.createTime)),
        c: index,
        d: common_vendor.o(($event) => $options.goToNoticeDetail(item.id), index)
      };
    })
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
