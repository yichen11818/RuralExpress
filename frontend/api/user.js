/**
 * 用户相关的API服务
 */
import request from '@/utils/request';

/**
 * 获取当前登录用户的个人资料
 * @returns {Promise} 用户资料
 */
export function getUserProfile() {
  // 从本地存储获取当前用户信息
  const userInfo = uni.getStorageSync('userInfo');
  if (!userInfo || !userInfo.id) {
    return Promise.reject(new Error('用户未登录或无法获取用户ID'));
  }
  // 使用实际的数字ID
  return request.get(`/user/${userInfo.id}`);
}

/**
 * 更新当前登录用户的个人资料
 * @param {Object} data 用户资料数据
 * @returns {Promise} 更新结果
 */
export function updateUserProfile(data) {
  // 从本地存储获取当前用户信息
  const userInfo = uni.getStorageSync('userInfo');
  if (!userInfo || !userInfo.id) {
    return Promise.reject(new Error('用户未登录或无法获取用户ID'));
  }
  // 使用实际的数字ID
  return request.put(`/user/${userInfo.id}`, data);
}

/**
 * 获取用户信息
 * @param {Number} id 用户ID
 * @returns {Promise} 用户信息
 */
export function getUserById(id) {
  return request.get(`/user/${id}`);
}

/**
 * 更新用户信息
 * @param {Number} id 用户ID
 * @param {Object} data 用户信息
 * @returns {Promise} 更新结果
 */
export function updateUser(id, data) {
  return request.put(`/user/${id}`, data);
}

/**
 * 实名认证
 * @param {Number} id 用户ID
 * @param {Object} data 认证信息
 * @returns {Promise} 认证结果
 */
export function verifyUser(id, data) {
  return request.post(`/user/${id}/verify`, data);
}

/**
 * 获取用户地址列表
 * @returns {Promise} 地址列表
 */
export function getAddressList() {
  return request.get('/addresses');
}

/**
 * 获取地址详情
 * @param {Number} id 地址ID
 * @returns {Promise} 地址详情
 */
export function getAddressById(id) {
  return request.get(`/addresses/${id}`);
}

/**
 * 获取默认地址
 * @returns {Promise} 默认地址
 */
export function getDefaultAddress() {
  return request.get('/addresses/default');
}

/**
 * 添加地址
 * @param {Object} data 地址信息
 * @returns {Promise} 添加后的地址
 */
export function addAddress(data) {
  return request.post('/addresses', data);
}

/**
 * 更新地址
 * @param {Number} id 地址ID
 * @param {Object} data 地址信息
 * @returns {Promise} 更新结果
 */
export function updateAddress(id, data) {
  return request.put(`/addresses/${id}`, data);
}

/**
 * 删除地址
 * @param {Number} id 地址ID
 * @returns {Promise} 删除结果
 */
export function deleteAddress(id) {
  return request.delete(`/addresses/${id}`);
}

/**
 * 设置默认地址
 * @param {Number} id 地址ID
 * @returns {Promise} 设置结果
 */
export function setDefaultAddress(id) {
  return request.put(`/addresses/${id}/default`);
} 