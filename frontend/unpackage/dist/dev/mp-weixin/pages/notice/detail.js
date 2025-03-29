"use strict";
const common_vendor = require("../../common/vendor.js");
const api_notice = require("../../api/notice.js");
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
      relatedNotices: [],
      loadError: false,
      errorMessage: ""
    };
  },
  onLoad(options) {
    console.log("API对象结构:", this.$api);
    console.log("Notice API结构:", this.$api.notice);
    console.log("直接导入的noticeApi:", api_notice.notice);
    if (options.id) {
      this.id = options.id;
      this.getNoticeDetail();
      this.getRelatedNotices();
    } else {
      this.setError("公告ID不存在");
    }
  },
  methods: {
    // 设置错误状态
    setError(message) {
      this.loadError = true;
      this.errorMessage = message || "加载公告失败";
    },
    // 返回上一页
    goBack() {
      common_vendor.index.navigateBack();
    },
    // 获取公告详情
    getNoticeDetail() {
      common_vendor.index.showLoading({
        title: "加载中"
      });
      api_notice.notice.detail(this.id).then((res) => {
        if (res.code === 200) {
          this.noticeData = res.data;
        } else {
          this.setError(res.message || "获取公告详情失败");
          common_vendor.index.showToast({
            title: res.message || "获取公告详情失败",
            icon: "none"
          });
        }
      }).catch((err) => {
        console.error("获取公告详情失败:", err);
        this.setError("网络错误，请稍后重试");
        common_vendor.index.showToast({
          title: "网络错误，请稍后重试",
          icon: "none"
        });
      }).finally(() => {
        common_vendor.index.hideLoading();
      });
    },
    // 获取相关公告
    getRelatedNotices() {
      api_notice.notice.related({
        id: this.id,
        limit: 5
      }).then((res) => {
        if (res.code === 200) {
          this.relatedNotices = res.data;
        }
      }).catch((err) => {
        console.error("获取相关公告失败:", err);
      });
    },
    // 格式化日期
    formatDate(dateString) {
      if (!dateString)
        return "";
      try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
          const convertedDate = new Date(dateString.replace(/-/g, "/"));
          if (isNaN(convertedDate.getTime())) {
            console.warn("无法解析日期:", dateString);
            return dateString;
          }
          return this.formatDateObject(convertedDate);
        }
        return this.formatDateObject(date);
      } catch (error) {
        console.error("日期格式化错误:", error);
        return dateString;
      }
    },
    // 格式化日期对象
    formatDateObject(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hour = String(date.getHours()).padStart(2, "0");
      const minute = String(date.getMinutes()).padStart(2, "0");
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    // 跳转到另一个公告详情
    goToNoticeDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/notice/detail?id=${id}`
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
    a: $data.loadError
  }, $data.loadError ? {
    b: common_vendor.p({
      type: "info",
      size: "64",
      color: "#FF6B35"
    }),
    c: common_vendor.t($data.errorMessage),
    d: common_vendor.o((...args) => $options.goBack && $options.goBack(...args))
  } : common_vendor.e({
    e: common_vendor.t($data.noticeData.title),
    f: common_vendor.t($options.formatDate($data.noticeData.createTime)),
    g: common_vendor.t($data.noticeData.source || "乡递通"),
    h: $data.noticeData.content,
    i: $data.relatedNotices.length > 0
  }, $data.relatedNotices.length > 0 ? {
    j: common_vendor.f($data.relatedNotices, (item, index, i0) => {
      return {
        a: common_vendor.t(item.title),
        b: common_vendor.t($options.formatDate(item.createTime)),
        c: index,
        d: common_vendor.o(($event) => $options.goToNoticeDetail(item.id), index)
      };
    })
  } : {}));
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
