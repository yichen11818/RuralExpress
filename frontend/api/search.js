/**
 * 搜索相关的API服务
 */
import request from '@/utils/request';

/**
 * 通用搜索
 * @param {String} keyword 搜索关键词
 * @param {Object} params 搜索参数
 * @returns {Promise} 搜索结果
 */
export function search(keyword, params = {}) {
  return request.get('/api/search', {
    params: {
      keyword,
      ...params
    }
  });
}

/**
 * 搜索包裹/运单
 * @param {String} keyword 搜索关键词
 * @param {Number} page 页码
 * @param {Number} pageSize 每页条数
 * @returns {Promise} 搜索结果
 */
export function searchPackages(keyword, page = 1, pageSize = 10) {
  return request.get('/api/search/packages', {
    params: {
      keyword,
      page,
      pageSize
    }
  });
}

/**
 * 搜索订单
 * @param {String} keyword 搜索关键词
 * @param {Number} page 页码
 * @param {Number} pageSize 每页条数
 * @returns {Promise} 搜索结果
 */
export function searchOrders(keyword, page = 1, pageSize = 10) {
  return request.get('/api/search/orders', {
    params: {
      keyword,
      page,
      pageSize
    }
  });
}

/**
 * 搜索快递员
 * @param {String} keyword 搜索关键词
 * @param {Number} page 页码
 * @param {Number} pageSize 每页条数
 * @returns {Promise} 搜索结果
 */
export function searchCouriers(keyword, page = 1, pageSize = 10) {
  return request.get('/api/search/couriers', {
    params: {
      keyword,
      page,
      pageSize
    }
  });
}

/**
 * 搜索服务点
 * @param {String} keyword 搜索关键词
 * @param {Number} page 页码
 * @param {Number} pageSize 每页条数
 * @returns {Promise} 搜索结果
 */
export function searchStations(keyword, page = 1, pageSize = 10) {
  return request.get('/api/search/stations', {
    params: {
      keyword,
      page,
      pageSize
    }
  });
} 