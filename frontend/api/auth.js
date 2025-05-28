/**
 * 认证相关的API服务
 */
import request from '@/utils/request';

/**
 * 用户登录
 * @param {Object} data 登录信息
 * @returns {Promise} 登录结果
 */
export function login(data) {
  return request.post('/auth/login', data);
}

/**
 * 用户注册
 * @param {Object} data 注册信息
 * @returns {Promise} 注册结果
 */
export function register(data) {
  return request.post('/user/register', data);
}

/**
 * 保存登录信息到本地存储
 * @param {Object} loginData 登录返回的数据
 */
export function saveLoginInfo(loginData) {
  // 保存令牌
  uni.setStorageSync('token', loginData.token);
  // 保存用户信息
  uni.setStorageSync('userInfo', loginData.user);
}

/**
 * 获取当前登录用户信息
 * @returns {Object|null} 用户信息或null
 */
export function getUserInfo() {
  return uni.getStorageSync('userInfo') || null;
}

/**
 * 是否已登录
 * @returns {Boolean} 是否已登录
 */
export function isLoggedIn() {
  return !!uni.getStorageSync('token');
}

/**
 * 退出登录
 */
export function logout() {
  // 清除本地存储
  uni.removeStorageSync('token');
  uni.removeStorageSync('userInfo');
  
  // 跳转到登录页
  uni.reLaunch({
    url: '/pages/login/login'
  });
}

/**
 * 更新用户信息
 * @param {Object} userInfo 用户信息
 */
export function updateUserInfo(userInfo) {
  // 保存更新后的用户信息到本地存储
  uni.setStorageSync('userInfo', userInfo);
  
  // todo 
  // 这里可以添加向服务器发送请求更新用户信息的代码
  // 例如:
  // return request.put('/user/profile', userInfo);
} 