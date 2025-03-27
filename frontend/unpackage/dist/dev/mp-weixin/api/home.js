"use strict";
const utils_request = require("../utils/request.js");
function getHomeData() {
  return utils_request.request.get("/home");
}
function getNearestCouriers(limit = 5, latitude, longitude) {
  const params = { limit };
  if (latitude !== void 0 && longitude !== void 0) {
    params.latitude = latitude;
    params.longitude = longitude;
  }
  return utils_request.request.get("/home/couriers/nearest", params);
}
exports.getHomeData = getHomeData;
exports.getNearestCouriers = getNearestCouriers;
