/**
 * 支付相关API
 */
import request from '@/utils/request';

/**
 * 订单支付
 * @param {Object} data 支付参数
 * @returns {Promise} 支付结果
 */
export function payOrder(data) {
  return request.post('/payment/pay', data);
}

/**
 * 查询支付状态
 * @param {Number} orderId 订单ID
 * @returns {Promise} 支付状态
 */
export function getPaymentStatus(orderId) {
  return request.get(`/payment/status/${orderId}`);
}

/**
 * 取消支付
 * @param {Number} orderId 订单ID
 * @returns {Promise} 取消结果
 */
export function cancelPayment(orderId) {
  return request.put(`/payment/cancel/${orderId}`);
}

/**
 * 获取用户钱包信息
 * @returns {Promise} 钱包信息
 */
export function getUserWallet() {
  return request.get('/payment/wallet');
}

/**
 * 查询支付渠道
 * @returns {Promise} 支付渠道列表
 */
export function getPaymentChannels() {
  return request.get('/payment/channels');
}

/**
 * 模拟支付成功回调（开发环境使用）
 * @param {Number} orderId 订单ID
 * @returns {Promise} 模拟支付成功结果
 */
export function mockPaymentSuccess(orderId) {
  return request.post(`/payment/mock/success/${orderId}`);
} 