"use strict";
const common_vendor = require("../common/vendor.js");
const utils_request = require("../utils/request.js");
function getUserProfile() {
  const userInfo = common_vendor.index.getStorageSync("userInfo");
  if (!userInfo || !userInfo.id) {
    return Promise.reject(new Error("用户未登录或无法获取用户ID"));
  }
  return utils_request.request.get(`/api/user/${userInfo.id}`);
}
function updateUserProfile(data) {
  const userInfo = common_vendor.index.getStorageSync("userInfo");
  if (!userInfo || !userInfo.id) {
    return Promise.reject(new Error("用户未登录或无法获取用户ID"));
  }
  return utils_request.request.put(`/api/user/${userInfo.id}`, data);
}
function getUserById(id) {
  return utils_request.request.get(`/api/user/${id}`);
}
function updateUser(id, data) {
  return utils_request.request.put(`/api/user/${id}`, data);
}
function verifyUser(id, data) {
  return utils_request.request.post(`/api/user/${id}/verify`, data);
}
function getAddressList() {
  return utils_request.request.get("/api/addresses");
}
function getAddressById(id) {
  return utils_request.request.get(`/api/addresses/${id}`);
}
function getDefaultAddress() {
  return utils_request.request.get("/api/addresses/default");
}
function addAddress(data) {
  return utils_request.request.post("/api/addresses", data);
}
function updateAddress(id, data) {
  return utils_request.request.put(`/api/addresses/${id}`, data);
}
function deleteAddress(id) {
  return utils_request.request.delete(`/api/addresses/${id}`);
}
function setDefaultAddress(id) {
  return utils_request.request.put(`/api/addresses/${id}/default`);
}
const user = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  addAddress,
  deleteAddress,
  getAddressById,
  getAddressList,
  getDefaultAddress,
  getUserById,
  getUserProfile,
  setDefaultAddress,
  updateAddress,
  updateUser,
  updateUserProfile,
  verifyUser
}, Symbol.toStringTag, { value: "Module" }));
exports.getAddressList = getAddressList;
exports.getUserProfile = getUserProfile;
exports.updateUserProfile = updateUserProfile;
exports.user = user;
exports.verifyUser = verifyUser;
