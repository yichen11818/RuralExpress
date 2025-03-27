import request from '@/utils/request';

/**
 * 获取首页数据
 * @returns {Promise} 首页数据，包括轮播图、公告、周边快递员等
 */
export function getHomeData() {
  return request.get('/home');
}

/**
 * 获取轮播图列表
 * @returns {Promise} 轮播图列表
 */
export function getBanners() {
  return request.get('/home/banners');
}

/**
 * 获取公告列表
 * @returns {Promise} 公告列表
 */
export function getNotices() {
  return request.get('/home/notices');
}

/**
 * 获取附近快递员
 * @param {Number} limit 获取数量
 * @returns {Promise} 快递员列表
 */
export function getNearestCouriers(limit = 5) {
  return request.get('/home/couriers/nearest', { limit });
} 