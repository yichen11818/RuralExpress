"use strict";
const utils_request = require("../utils/request.js");
function getHomeData() {
  return utils_request.request.get("/api/home");
}
exports.getHomeData = getHomeData;
