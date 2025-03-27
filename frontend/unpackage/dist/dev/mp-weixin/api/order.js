"use strict";
const utils_request = require("../utils/request.js");
function getUserOrders(userId, params = {}) {
  return utils_request.request.get(`/api/order/user/${userId}`, params);
}
function cancelOrder(orderId, reason) {
  return utils_request.request.put(`/order/${orderId}/cancel?reason=${encodeURIComponent(reason)}`);
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
exports.cancelOrder = cancelOrder;
exports.getOrderStatusText = getOrderStatusText;
exports.getUserOrders = getUserOrders;
