"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://localhost:8080";
console.log("[请求工具] API基础URL:", BASE_URL);
console.log("[请求工具] 当前环境:", "development");
console.log("[请求工具] 平台信息:", common_vendor.index.getSystemInfoSync());
const logRequest = (message, data) => {
  console.log(`[请求日志] ${message}`, data);
};
const NO_AUTH_PATHS = [
  "/auth/login",
  "/user/register"
];
const requestInterceptor = (config) => {
  logRequest("发起请求", { url: config.url, method: config.method });
  const path = config.url.replace(BASE_URL, "");
  NO_AUTH_PATHS.some((p) => path.includes(p));
  const token = common_vendor.index.getStorageSync("token");
  if (token) {
    config.header = {
      ...config.header,
      "Authorization": `Bearer ${token}`
    };
  }
  logRequest("请求配置", config);
  return config;
};
const responseInterceptor = (response) => {
  logRequest("收到响应", response);
  if (response.statusCode === 200) {
    const responseData = response.data;
    if (responseData && typeof responseData === "object") {
      if (responseData.hasOwnProperty("code")) {
        if (responseData.code === 200) {
          logRequest("请求成功", responseData);
          return responseData;
        } else if (responseData.code === 401) {
          logRequest("授权失败", responseData);
          common_vendor.index.showToast({
            title: "请先登录",
            icon: "none"
          });
          common_vendor.index.navigateTo({
            url: "/pages/login/login"
          });
          return Promise.reject(responseData);
        } else {
          logRequest("业务错误", responseData);
          common_vendor.index.showToast({
            title: responseData.message || "请求失败",
            icon: "none"
          });
          return Promise.reject(responseData);
        }
      } else {
        const result = { data: responseData, code: 200 };
        logRequest("请求成功(直接数据)", result);
        return result;
      }
    } else {
      const result = { data: responseData, code: 200 };
      logRequest("请求成功(直接数据)", result);
      return result;
    }
  } else {
    logRequest("HTTP错误", { statusCode: response.statusCode, data: response.data });
    common_vendor.index.showToast({
      title: "网络错误: " + response.statusCode,
      icon: "none"
    });
    return Promise.reject(response);
  }
};
const request = (options) => {
  const mergedOptions = {
    url: `${BASE_URL}${options.url}`,
    method: options.method || "GET",
    data: options.data,
    header: {
      "Content-Type": "application/json",
      ...options.header
    }
  };
  const finalOptions = requestInterceptor(mergedOptions);
  return new Promise((resolve, reject) => {
    logRequest("执行请求", finalOptions);
    common_vendor.index.request({
      ...finalOptions,
      success: (response) => {
        try {
          const result = responseInterceptor(response);
          resolve(result);
        } catch (error) {
          logRequest("响应处理异常", error);
          reject(error);
        }
      },
      fail: (error) => {
        logRequest("请求失败", error);
        console.error("[网络请求详细错误]", {
          url: finalOptions.url,
          method: finalOptions.method,
          error
        });
        common_vendor.index.showToast({
          title: "网络连接失败",
          icon: "none"
        });
        reject(error);
      }
    });
  });
};
const request$1 = {
  // 测试网络连接状态
  testConnection: () => {
    console.log("[请求工具] 开始测试服务器连接...");
    return new Promise((resolve) => {
      common_vendor.index.request({
        url: `${BASE_URL}/api/health`,
        method: "GET",
        timeout: 5e3,
        success: (res) => {
          console.log("[请求工具] 服务器连接测试成功:", res);
          resolve({
            success: true,
            statusCode: res.statusCode,
            data: res.data
          });
        },
        fail: (error) => {
          console.error("[请求工具] 服务器连接测试失败:", error);
          resolve({
            success: false,
            error
          });
        }
      });
    });
  },
  get: (url, params = {}) => {
    const queryParams = params.params || params;
    let queryString = "";
    if (Object.keys(queryParams).length > 0) {
      const parts = [];
      for (const key in queryParams) {
        if (queryParams[key] !== void 0 && queryParams[key] !== null) {
          if (Array.isArray(queryParams[key])) {
            queryParams[key].forEach((value) => {
              parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`);
            });
          } else {
            parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(queryParams[key])}`);
          }
        }
      }
      if (parts.length > 0) {
        queryString = "?" + parts.join("&");
      }
    }
    return request({
      url: url + queryString,
      method: "GET"
    });
  },
  post: (url, data = {}) => {
    return request({
      url,
      method: "POST",
      data
    });
  },
  put: (url, data = {}) => {
    return request({
      url,
      method: "PUT",
      data
    });
  },
  delete: (url, data = {}) => {
    return request({
      url,
      method: "DELETE",
      data
    });
  }
};
exports.request = request$1;
