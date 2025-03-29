/**
 * 订单相关的API服务
 */
import request from '@/utils/request';

/**
 * 创建订单
 * @param {Object} data 订单信息
 * @returns {Promise} 创建结果
 */
export function createOrder(data) {
  return request.post('/api/order', data);
}

/**
 * 获取订单详情
 * @param {Number} id 订单ID
 * @returns {Promise} 订单详情
 */
export function getOrderDetail(id) {
  return request.get(`/api/order/${id}`);
}

/**
 * 获取用户订单列表
 * @param {Number} userId 用户ID
 * @param {Object} params 查询参数
 * @returns {Promise} 订单列表
 */
export function getUserOrders(userId, params = {}) {
  return request.get(`/api/order/user/${userId}`, { params });
}

/**
 * 获取快递员订单列表
 * @param {Number} courierId 快递员ID
 * @param {Object} params 查询参数
 * @returns {Promise} 订单列表
 */
export function getCourierOrders(courierId, params = {}) {
  return request.get(`/api/order/courier/${courierId}`, { params });
}

/**
 * 获取待接单列表
 * @param {Object} params 查询参数
 * @returns {Promise} 订单列表
 */
export function getPendingOrders(params) {
  return request.get('/api/order/pending', { params });
}

/**
 * 快递员接单
 * @param {Number} orderId 订单ID
 * @param {Number} courierId 快递员ID
 * @returns {Promise} 接单结果
 */
export function acceptOrder(orderId, courierId) {
  return request.put(`/api/order/${orderId}/accept?courierId=${courierId}`);
}

/**
 * 更新订单状态
 * @param {Number} orderId 订单ID
 * @param {Number} status 订单状态
 * @returns {Promise} 更新结果
 */
export function updateOrderStatus(orderId, status) {
  return request.put(`/api/order/${orderId}/status?status=${status}`);
}

/**
 * 取消订单
 * @param {Number} orderId 订单ID
 * @param {String} reason 取消原因
 * @returns {Promise} 取消结果
 */
export function cancelOrder(orderId, reason) {
  return request.put(`/api/order/${orderId}/cancel?reason=${encodeURIComponent(reason)}`);
}

/**
 * 更新订单信息
 * @param {Number} orderId 订单ID
 * @param {Object} data 更新信息
 * @returns {Promise} 更新结果
 */
export function updateOrder(orderId, data) {
  return request.put(`/api/order/${orderId}`, data);
}

/**
 * 获取订单状态文本
 * @param {Number} status 订单状态码
 * @returns {String} 状态文本
 */
export function getOrderStatusText(status) {
  const statusMap = {
    0: '待接单',
    1: '已接单',
    2: '取件中',
    3: '已取件',
    4: '配送中',
    5: '已送达',
    6: '已完成',
    7: '已取消'
  };
  return statusMap[status] || '未知状态';
}

/**
 * 计算订单价格
 * @param {Number} distance 距离(公里)
 * @param {Number} packageType 包裹类型
 * @returns {Number} 价格
 */
export function calculateOrderPrice(distance, packageType) {
  // 基础价格
  const basePrice = 5;
  
  // 距离费用: 每公里1元
  const distanceFee = Math.max(0, distance - 2) * 1;
  
  // 包裹类型附加费
  const packageTypeFee = packageType === 0 ? 0 : (packageType === 1 ? 3 : 6);
  
  // 总价
  return basePrice + distanceFee + packageTypeFee;
}

/**
 * 获取包裹类型文本
 * @param {Number} type 包裹类型
 * @returns {String} 类型文本
 */
export function getPackageTypeText(type) {
  const typeMap = {
    0: '小件',
    1: '中件',
    2: '大件'
  };
  return typeMap[type] || '未知类型';
}

/**
 * 提交订单评价
 * @param {Object} data 评价信息
 * @returns {Promise} 提交结果
 */
export function submitOrderReview(data) {
  return request.post('/api/order/review', data);
}

/**
 * 获取订单评价详情
 * @param {Number} orderId 订单ID
 * @returns {Promise} 评价详情
 */
export function getOrderReview(orderId) {
  return request.get(`/api/order/${orderId}/review`);
}

/**
 * 上传评价图片
 * @param {File} file 图片文件
 * @returns {Promise} 上传结果
 */
export function uploadReviewImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/api/order/review/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }); 
}

/**
 * 获取物流跟踪信息
 * @param {Object} params 查询参数（trackingNo或orderId）
 * @returns {Promise} 物流跟踪信息
 */
export function getLogisticsInfo(params) {
  return request.get('/api/order/logistics', { params });
}

/**
 * 获取用户的物流追踪列表
 * @param {Object} params 查询参数
 * @returns {Promise} 物流追踪列表
 */
export function getTrackingList(params = {}) {
  return request.get('/api/order/tracking/list', { params });
}