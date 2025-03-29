"use strict";
const utils_request = require("../utils/request.js");
function createOrder(data) {
  return utils_request.request.post("/api/order", data);
}
function getOrderDetail(id) {
  return utils_request.request.get(`/api/order/${id}`);
}
function getUserOrders(userId, params = {}) {
  return utils_request.request.get(`/api/order/user/${userId}`, { params });
}
function getCourierOrders(courierId, params = {}) {
  return utils_request.request.get(`/api/order/courier/${courierId}`, { params });
}
function getPendingOrders(params) {
  return utils_request.request.get("/api/order/pending", { params });
}
function acceptOrder(orderId, courierId) {
  return utils_request.request.put(`/api/order/${orderId}/accept?courierId=${courierId}`);
}
function updateOrderStatus(orderId, status) {
  return utils_request.request.put(`/api/order/${orderId}/status?status=${status}`);
}
function cancelOrder(orderId, reason) {
  return utils_request.request.put(`/api/order/${orderId}/cancel?reason=${encodeURIComponent(reason)}`);
}
function updateOrder(orderId, data) {
  return utils_request.request.put(`/api/order/${orderId}`, data);
}
function getOrderStatusText(status) {
  const statusMap = {
    0: "待接单",
    1: "已接单",
    2: "取件中",
    3: "已取件",
    4: "配送中",
    5: "已送达",
    6: "已完成",
    7: "已取消"
  };
  return statusMap[status] || "未知状态";
}
function calculateOrderPrice(distance, packageType) {
  const basePrice = 5;
  const distanceFee = Math.max(0, distance - 2) * 1;
  const packageTypeFee = packageType === 0 ? 0 : packageType === 1 ? 3 : 6;
  return basePrice + distanceFee + packageTypeFee;
}
function getPackageTypeText(type) {
  const typeMap = {
    0: "小件",
    1: "中件",
    2: "大件"
  };
  return typeMap[type] || "未知类型";
}
function submitOrderReview(data) {
  return utils_request.request.post("/api/order/review", data);
}
function getOrderReview(orderId) {
  return utils_request.request.get(`/api/order/${orderId}/review`);
}
function uploadReviewImage(file) {
  const formData = new FormData();
  formData.append("file", file);
  return utils_request.request.post("/api/order/review/upload", formData, {
    headers: {
      "Content-Type": "multipart/form-data"
    }
  });
}
function getLogisticsInfo(params) {
  return utils_request.request.get("/api/order/logistics", { params });
}
const order = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  acceptOrder,
  calculateOrderPrice,
  cancelOrder,
  createOrder,
  getCourierOrders,
  getLogisticsInfo,
  getOrderDetail,
  getOrderReview,
  getOrderStatusText,
  getPackageTypeText,
  getPendingOrders,
  getUserOrders,
  submitOrderReview,
  updateOrder,
  updateOrderStatus,
  uploadReviewImage
}, Symbol.toStringTag, { value: "Module" }));
exports.cancelOrder = cancelOrder;
exports.createOrder = createOrder;
exports.getLogisticsInfo = getLogisticsInfo;
exports.getOrderStatusText = getOrderStatusText;
exports.getUserOrders = getUserOrders;
exports.order = order;
