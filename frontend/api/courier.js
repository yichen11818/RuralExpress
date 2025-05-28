/**
 * 快递员相关的API服务
 */
import request from '@/utils/request';
import { getToken } from '@/utils/auth';

/**
 * 申请成为快递员
 * @param {Object} data 申请信息
 * @returns {Promise} 申请结果
 */
export function applyCourier(data) {
  console.log('调用快递员申请API:', data);
  return request.post('/courier/apply', data)
    .then(res => {
      console.log('快递员申请API响应:', res);
      return res;
    })
    .catch(err => {
      console.error('快递员申请API错误:', err);
      throw err;
    });
}   

/**
 * 获取快递员信息
 * @param {Number} id 快递员ID
 * @returns {Promise} 快递员信息
 */
export function getCourierInfo(id) {
  return request.get(`/courier/${id}`);
}

/**
 * 根据用户ID获取快递员信息
 * @param {Number} userId 用户ID
 * @returns {Promise} 快递员信息
 */
export function getCourierByUserId(userId) {
  return request.get(`/courier/user/${userId}`);
}

/**
 * 更新快递员信息
 * @param {Number} id 快递员ID
 * @param {Object} data 更新信息
 * @returns {Promise} 更新结果
 */
export function updateCourier(id, data) {
  return request.put(`/courier/${id}`, data);
}

/**
 * 更新快递员服务状态
 * @param {Number} id 快递员ID
 * @param {Number} status 服务状态(0-休息,1-接单中)
 * @returns {Promise} 更新结果
 */
export function updateServiceStatus(id, status) {
  return request.put(`/courier/${id}/status?status=${status}`);
}

/**
 * 查询附近的快递员
 * @param {Number} longitude 经度
 * @param {Number} latitude 纬度
 * @param {Number} distance 距离(公里)
 * @returns {Promise} 快递员列表
 */
export function getNearby(longitude, latitude, distance = 5) {
  return request.get(`/courier/nearby?longitude=${longitude}&latitude=${latitude}&distance=${distance}`);
}

/**
 * 获取快递员列表
 * @param {Object} params 查询参数
 * @returns {Promise} 快递员列表
 */
export function list(params) {
  return request.get('/courier/list', { params });
}

/**
 * 快递员评分计算
 * @param {Number} score 分数
 * @param {Number} count 评价数量
 * @returns {String} 格式化的评分
 */
export function formatRating(score, count = 0) {
  if (!score) {
    return '暂无评分';
  }
  const rating = parseFloat(score).toFixed(1);
  return `${rating}分 · ${count}条评价`;
}

/**
 * 获取快递员标签列表
 * @param {Number} courierId 快递员ID
 * @returns {Promise} 标签列表
 */
export function getCourierTags(courierId) {
  return request.get(`/courier/tags?courierId=${courierId}`);
}

/**
 * 解析快递员工作时间
 * @param {String} startTime 开始时间
 * @param {String} endTime 结束时间
 * @returns {String} 格式化的工作时间
 */
export function formatWorkTime(startTime, endTime) {
  if (!startTime || !endTime) {
    return '工作时间未设置';
  }
  return `${startTime} - ${endTime}`;
}

/**
 * 审核快递员申请
 * @param {Number} courierId 快递员ID
 * @param {Number} auditStatus 审核状态(1-通过,2-拒绝)
 * @param {String} auditRemark 审核备注
 * @returns {Promise} 审核结果
 */
export function auditCourier(courierId, auditStatus, auditRemark = '') {
  return request.put(`/courier/${courierId}/audit?status=${auditStatus}&reason=${auditRemark}`);
} 