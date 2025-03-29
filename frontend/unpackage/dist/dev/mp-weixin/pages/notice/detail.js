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
      common_vendor.index.request({
        url: this.$api.notice.detail,
        method: "GET",
        data: {
          id: this.id
        },
        success: (res) => {
          if (res.data.code === 200) {
            this.noticeData = res.data.data;
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "获取公告详情失败",
              icon: "none"
            });
          }
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "网络错误，请稍后重试",
            icon: "none"
          });
        },
        complete: () => {
          common_vendor.index.hideLoading();
        }
      });
    },
    // 获取相关公告
    getRelatedNotices() {
      common_vendor.index.request({
        url: this.$api.notice.related,
        method: "GET",
        data: {
          id: this.id,
          limit: 5
        },
        success: (res) => {
          if (res.data.code === 200) {
            this.relatedNotices = res.data.data;
          }
        }
      });
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
