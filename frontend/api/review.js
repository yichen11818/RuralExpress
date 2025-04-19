/**
 * 评价相关的API服务
 */
import request from '@/utils/request';

/**
 * 获取快递员评价列表
 * @param {Number} courierId 快递员ID
 * @param {Object} params 查询参数 {page, size, rating}
 * @returns {Promise} 评价列表
 */
export function getCourierReviews(courierId, params = {}) {
  return request.get(`/api/review/courier/${courierId}`, { params });
}

/**
 * 提交评价
 * @param {Object} data 评价数据
 * @returns {Promise} 提交结果
 */
export function submitReview(data) {
  return request.post('/api/review', data);
}

/**
 * 快递员回复评价
 * @param {Number} reviewId 评价ID
 * @param {String} reply 回复内容
 * @returns {Promise} 回复结果
 */
export function replyToReview(reviewId, reply) {
  return request.post(`/api/review/${reviewId}/reply`, { reply });
}

/**
 * 获取用户的评价记录
 * @param {Object} params 查询参数 {page, size}
 * @returns {Promise} 评价列表
 */
export function getUserReviews(params = {}) {
  return request.get('/api/review/user', { params });
} 