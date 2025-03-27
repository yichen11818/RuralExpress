"use strict";
const common_vendor = require("../common/vendor.js");
function isLoggedIn() {
  try {
    const token = common_vendor.index.getStorageSync("token");
    return !!token;
  } catch (e) {
    console.error("检查登录状态失败", e);
    return false;
  }
}
function checkLogin(redirect = true) {
  const loggedIn = isLoggedIn();
  if (!loggedIn && redirect) {
    common_vendor.index.navigateTo({
      url: "/pages/login/login"
    });
  }
  return loggedIn;
}
exports.checkLogin = checkLogin;
