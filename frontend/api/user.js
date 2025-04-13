/**
 * 用户相关的API服务 确保添加/api前缀!!1请勿去除!
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
  // 使用实际的数字ID，确保添加/api前缀
  return request.get(`/api/user/${userInfo.id}`);
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
  return request.put(`/api/user/${userInfo.id}`, data);
}

/**
 * 获取用户信息
 * @param {Number} id 用户ID
 * @returns {Promise} 用户信息
 */
export function getUserById(id) {
  return request.get(`/api/user/${id}`);
}

/**
 * 更新用户信息
 * @param {Number} id 用户ID
 * @param {Object} data 用户信息
 * @returns {Promise} 更新结果
 */
export function updateUser(id, data) {
  return request.put(`/api/user/${id}`, data);
}

/**
 * 实名认证
 * @param {Number} id 用户ID
 * @param {Object} data 认证信息
 * @returns {Promise} 认证结果
 */
export function verifyUser(id, data) {
  return request.post(`/api/user/${id}/verify`, data);
}

/**
 * 获取用户地址列表
 * @returns {Promise} 地址列表
 */
export function getAddressList() {
  return request.get('/api/addresses');
}

/**
 * 获取地址详情
 * @param {Number} id 地址ID
 * @returns {Promise} 地址详情
 */
export function getAddressById(id) {
  return request.get(`/api/addresses/${id}`);
}

/**
 * 获取默认地址
 * @returns {Promise} 默认地址
 */
export function getDefaultAddress() {
  return request.get('/api/addresses/default');
}

/**
 * 添加地址
 * @param {Object} data 地址信息
 * @returns {Promise} 添加后的地址
 */
export function addAddress(data) {
  return request.post('/api/addresses', data);
}

/**
 * 更新地址
 * @param {Number} id 地址ID
 * @param {Object} data 地址信息
 * @returns {Promise} 更新结果
 */
export function updateAddress(id, data) {
  return request.put(`/api/addresses/${id}`, data);
}

/**
 * 删除地址
 * @param {Number} id 地址ID
 * @returns {Promise} 删除结果
 */
export function deleteAddress(id) {
  return request.delete(`/api/addresses/${id}`);
}

/**
 * 设置默认地址
 * @param {Number} id 地址ID
 * @returns {Promise} 设置结果
 */
export function setDefaultAddress(id) {
  return request.put(`/api/addresses/${id}/default`);
}

/**
 * 修改密码
 * @param {Number} userId 用户ID
 * @param {String} oldPassword 旧密码
 * @param {String} newPassword 新密码
 * @returns {Promise} 修改结果
 */
export function changePassword(userId, oldPassword, newPassword) {
  const data = {
    oldPassword,
    newPassword
  };
  return request.post(`/api/user/${userId}/password`, data);
}

/**
 * 修改密码（别名，与页面保持一致）
 * @param {Number} userId 用户ID
 * @param {Object} data 包含oldPassword和newPassword的对象
 * @returns {Promise} 修改结果
 */
export function updatePassword(userId, data) {
  return request.post(`/api/user/${userId}/password`, data);
}

/**
 * 发送手机号变更验证码
 * @param {String} phone 手机号
 * @returns {Promise} 发送结果
 */
export function sendPhoneVerifyCode(phone) {
  return request.post(`/api/user/verifyCode/phone?phone=${encodeURIComponent(phone)}`);
}

/**
 * 更换手机号
 * @param {Number} userId 用户ID
 * @param {Object} data 包含newPhone和verifyCode的对象
 * @returns {Promise} 更换结果
 */
export function changePhone(userId, data) {
  return request.post(`/api/user/${userId}/phone`, data);
} 