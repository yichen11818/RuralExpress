/**
 * 支付相关API
 */
import { post, get, put } from '@/utils/request';

/**
 * 订单支付
 * @param {Object} data 支付参数
 * @returns {Promise} 支付结果
 */
export function payOrder(data) {
  return post('/api/payment/pay', data);
}

/**
 * 查询支付状态
 * @param {Number} orderId 订单ID
 * @returns {Promise} 支付状态
 */
export function getPaymentStatus(orderId) {
  return get(`/api/payment/status/${orderId}`);
}

/**
 * 取消支付
 * @param {Number} orderId 订单ID
 * @returns {Promise} 取消结果
 */
export function cancelPayment(orderId) {
  return put(`/api/payment/cancel/${orderId}`);
}

/**
 * 获取用户钱包信息
 * @returns {Promise} 钱包信息
 */
export function getUserWallet() {
  return get('/api/payment/wallet');
}

/**
 * 查询支付渠道
 * @returns {Promise} 支付渠道列表
 */
export function getPaymentChannels() {
  return get('/api/payment/channels');
} 