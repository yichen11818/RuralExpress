"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://localhost:8080/api";
const NO_AUTH_PATHS = [
  "/auth/login",
  "/user/register"
];
const requestInterceptor = (config) => {
  const path = config.url.replace(BASE_URL, "");
  NO_AUTH_PATHS.some((p) => path.includes(p));
  const token = common_vendor.index.getStorageSync("token");
  if (token) {
    config.header = {
      ...config.header,
      "Authorization": `Bearer ${token}`
    };
  }
  return config;
};
const responseInterceptor = (response) => {
  if (response.statusCode === 200) {
    const responseData = response.data;
    if (responseData && typeof responseData === "object") {
      if (responseData.hasOwnProperty("code")) {
        if (responseData.code === 200) {
          return responseData;
        } else if (responseData.code === 401) {
          common_vendor.index.showToast({
            title: "请先登录",
            icon: "none"
          });
          common_vendor.index.navigateTo({
            url: "/pages/login/login"
          });
          return Promise.reject(responseData);
        } else {
          common_vendor.index.showToast({
            title: responseData.message || "请求失败",
            icon: "none"
          });
          return Promise.reject(responseData);
        }
      } else {
        return { data: responseData, code: 200 };
      }
    } else {
      return { data: responseData, code: 200 };
    }
  } else {
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
    common_vendor.index.request({
      ...finalOptions,
      success: (response) => {
        try {
          const result = responseInterceptor(response);
          resolve(result);
        } catch (error) {
          reject(error);
        }
      },
      fail: (error) => {
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
  get: (url, params = {}) => {
    return request({
      url,
      method: "GET",
      data: params
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
