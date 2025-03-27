"use strict";
const utils_request = require("../utils/request.js");
function getHomeData() {
  return utils_request.request.get("/home");
}
exports.getHomeData = getHomeData;
