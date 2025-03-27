/**
 * 用户相关的API服务
 */
import request from '@/utils/request';

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