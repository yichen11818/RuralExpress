"use strict";
const utils_request = require("../utils/request.js");
function getHomeData() {
  return utils_request.request.get("/api/home");
}
function getBanners() {
  return utils_request.request.get("/api/home/banners");
}
function getNotices() {
  return utils_request.request.get("/api/home/notices");
}
function getNearestCouriers(limit = 5, latitude, longitude) {
  const params = { limit };
  if (latitude !== void 0 && longitude !== void 0) {
    params.latitude = latitude;
    params.longitude = longitude;
  }
  return utils_request.request.get("/api/home/couriers/nearest", params);
}
const home = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  getBanners,
  getHomeData,
  getNearestCouriers,
  getNotices
}, Symbol.toStringTag, { value: "Module" }));
exports.getHomeData = getHomeData;
exports.getNearestCouriers = getNearestCouriers;
exports.home = home;
