/**
 * 网络请求工具
 * !!!! 非必要请勿修改  前端请改为 /api/ 同时注意后端控制器 已带/api/ 无需添加
 */
import { isLoggedIn } from './auth.js';

// API基础URL
const BASE_URL = 'http://localhost:8080/api';

// 记录环境信息和API基础URL
console.log('[请求工具] API基础URL:', BASE_URL);
console.log('[请求工具] 当前环境:', process.env.NODE_ENV || 'development');
console.log('[请求工具] 平台信息:', uni.getSystemInfoSync());

// 添加日志函数
const logRequest = (message, data) => {
  console.log(`[请求日志] ${message}`, data);
};

// 不需要认证的API路径
const NO_AUTH_PATHS = [
  '/auth/login',
  '/user/register'
];

// 请求拦截器
const requestInterceptor = (config) => {
  // 记录请求开始
  logRequest('发起请求', { url: config.url, method: config.method });
  
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
  
  // 记录完整请求配置
  logRequest('请求配置', config);
  
  return config;
};

// 响应拦截器
const responseInterceptor = (response) => {
  // 记录原始响应
  logRequest('收到响应', response);
  
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
          logRequest('请求成功', responseData);
          return responseData;
        } else if (responseData.code === 401) {
          // 未授权，跳转到登录页
          logRequest('授权失败', responseData);
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          uni.redirectTo({
            url: '/pages/login/login'
          });
          return Promise.reject(responseData);
        } else {
          // 其他错误
          logRequest('业务错误', responseData);
          uni.showToast({
            title: responseData.message || '请求失败',
            icon: 'none'
          });
          return Promise.reject(responseData);
        }
      } else {
        // 如果没有code字段，直接返回数据
        const result = { data: responseData, code: 200 };
        logRequest('请求成功(直接数据)', result);
        return result;
      }
    } else {
      const result = { data: responseData, code: 200 };
      logRequest('请求成功(直接数据)', result);
      return result;
    }
  } else {
    // 其他HTTP错误
    logRequest('HTTP错误', { statusCode: response.statusCode, data: response.data });
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
    logRequest('执行请求', finalOptions);
    uni.request({
      ...finalOptions,
      success: (response) => {
        try {
          const result = responseInterceptor(response);
          resolve(result);
        } catch (error) {
          logRequest('响应处理异常', error);
          reject(error);
        }
      },
      fail: (error) => {
        logRequest('请求失败', error);
        // 记录详细的连接错误信息
        console.error('[网络请求详细错误]', {
          url: finalOptions.url,
          method: finalOptions.method,
          error: error
        });
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
  // 测试网络连接状态
  testConnection: () => {
    console.log('[请求工具] 开始测试服务器连接...');
    return new Promise((resolve) => {
      uni.request({
        url: `${BASE_URL}/health`,
        method: 'GET',
        timeout: 5000,
        success: (res) => {
          console.log('[请求工具] 服务器连接测试成功:', res);
          resolve({
            success: true,
            statusCode: res.statusCode,
            data: res.data
          });
        },
        fail: (error) => {
          console.error('[请求工具] 服务器连接测试失败:', error);
          resolve({
            success: false,
            error: error
          });
        }
      });
    });
  },
  
  get: (url, params = {}) => {
    // 对于GET请求，params可以是普通对象或{params}格式
    const queryParams = params.params || params;
    
    // 构建URL查询参数字符串
    let queryString = '';
    if (Object.keys(queryParams).length > 0) {
      const parts = [];
      for (const key in queryParams) {
        if (queryParams[key] !== undefined && queryParams[key] !== null) {
          // 处理数组参数，将其转换为多个同名参数
          if (Array.isArray(queryParams[key])) {
            queryParams[key].forEach(value => {
              parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`);
            });
          } else {
            parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(queryParams[key])}`);
          }
        }
      }
      if (parts.length > 0) {
        queryString = '?' + parts.join('&');
      }
    }
    
    return request({
      url: url + queryString,
      method: 'GET'
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