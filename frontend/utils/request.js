/**
 * 网络请求工具
 */

// API基础URL
const BASE_URL = 'http://localhost:8080';

// 不需要认证的API路径
const NO_AUTH_PATHS = [
  '/auth/login',
  '/user/register'
];

// 请求拦截器
const requestInterceptor = (config) => {
  // 提取请求路径
  const path = config.url.replace(BASE_URL, '');
  
  // 判断是否是无需认证的路径
  const isNoAuthPath = NO_AUTH_PATHS.some(p => path.includes(p));
  
  // 从本地存储获取令牌
  const token = uni.getStorageSync('token');
  
  if (token) {
    // 已登录用户使用Bearer令牌
    config.header = {
      ...config.header,
      'Authorization': `Bearer ${token}`
    };
  }
  
  return config;
};

// 响应拦截器
const responseInterceptor = (response) => {
  // 请求成功
  if (response.statusCode === 200) {
    const responseData = response.data;
    
    // 检查返回格式，支持两种格式：
    // 1. { code: 200, data: {...}, message: '...' }
    // 2. 直接返回数据 {...}
    if (responseData && typeof responseData === 'object') {
      // 如果有code字段，按格式1处理
      if (responseData.hasOwnProperty('code')) {
        if (responseData.code === 200) {
          return responseData;
        } else if (responseData.code === 401) {
          // 未授权，跳转到登录页
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          uni.navigateTo({
            url: '/pages/login/login'
          });
          return Promise.reject(responseData);
        } else {
          // 其他错误
          uni.showToast({
            title: responseData.message || '请求失败',
            icon: 'none'
          });
          return Promise.reject(responseData);
        }
      } else {
        // 如果没有code字段，直接返回数据
        return { data: responseData, code: 200 };
      }
    } else {
      return { data: responseData, code: 200 };
    }
  } else {
    // 其他HTTP错误
    uni.showToast({
      title: '网络错误: ' + response.statusCode,
      icon: 'none'
    });
    return Promise.reject(response);
  }
};

// 封装请求方法
const request = (options) => {
  // 合并选项
  const mergedOptions = {
    url: `${BASE_URL}${options.url}`,
    method: options.method || 'GET',
    data: options.data,
    header: {
      'Content-Type': 'application/json',
      ...options.header
    }
  };
  
  // 应用请求拦截器
  const finalOptions = requestInterceptor(mergedOptions);
  
  // 发送请求
  return new Promise((resolve, reject) => {
    uni.request({
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
        uni.showToast({
          title: '网络连接失败',
          icon: 'none'
        });
        reject(error);
      }
    });
  });
};

// 导出请求方法
export default {
  get: (url, params = {}) => {
    return request({
      url,
      method: 'GET',
      data: params
    });
  },
  post: (url, data = {}) => {
    return request({
      url,
      method: 'POST',
      data
    });
  },
  put: (url, data = {}) => {
    return request({
      url,
      method: 'PUT',
      data
    });
  },
  delete: (url, data = {}) => {
    return request({
      url,
      method: 'DELETE',
      data
    });
  }
}; 