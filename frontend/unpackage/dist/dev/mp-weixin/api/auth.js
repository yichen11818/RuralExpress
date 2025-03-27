"use strict";
const common_vendor = require("../common/vendor.js");
const utils_request = require("../utils/request.js");
function login(data) {
  return utils_request.request.post("/auth/login", data);
}
function register(data) {
  return utils_request.request.post("/user/register", data);
}
function saveLoginInfo(loginData) {
  common_vendor.index.setStorageSync("token", loginData.token);
  common_vendor.index.setStorageSync("userInfo", loginData.user);
}
function getUserInfo() {
  return common_vendor.index.getStorageSync("userInfo") || null;
}
function isLoggedIn() {
  return !!common_vendor.index.getStorageSync("token");
}
function logout() {
  common_vendor.index.removeStorageSync("token");
  common_vendor.index.removeStorageSync("userInfo");
  common_vendor.index.reLaunch({
    url: "/pages/login/login"
  });
}
function updateUserInfo(userInfo) {
  common_vendor.index.setStorageSync("userInfo", userInfo);
}
exports.getUserInfo = getUserInfo;
exports.isLoggedIn = isLoggedIn;
exports.login = login;
exports.logout = logout;
exports.register = register;
exports.saveLoginInfo = saveLoginInfo;
exports.updateUserInfo = updateUserInfo;
