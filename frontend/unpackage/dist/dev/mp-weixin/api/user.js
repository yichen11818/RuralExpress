"use strict";
const common_vendor = require("../common/vendor.js");
const utils_request = require("../utils/request.js");
function getUserProfile() {
  const userInfo = common_vendor.index.getStorageSync("userInfo");
  if (!userInfo || !userInfo.id) {
    return Promise.reject(new Error("用户未登录或无法获取用户ID"));
  }
  return utils_request.request.get(`/user/${userInfo.id}`);
}
function updateUserProfile(data) {
  const userInfo = common_vendor.index.getStorageSync("userInfo");
  if (!userInfo || !userInfo.id) {
    return Promise.reject(new Error("用户未登录或无法获取用户ID"));
  }
  return utils_request.request.put(`/user/${userInfo.id}`, data);
}
exports.getUserProfile = getUserProfile;
exports.updateUserProfile = updateUserProfile;
