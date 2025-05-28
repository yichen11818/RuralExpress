import request from '@/utils/request'

const baseUrl = '/notice'

const notice = {
  /**
   * 获取公告列表
   * @param {Object} params - 请求参数
   * @param {number} params.page - 页码
   * @param {number} params.size - 每页条数
   * @param {string} params.keyword - 搜索关键词(可选)
   * @param {string} params.category - 公告类别(可选)
   * @returns {Promise} - 请求Promise
   */
  list(params) {
    return request.get(`${baseUrl}/list`, params)
  },

  /**
   * 获取公告详情
   * @param {string} id - 公告ID
   * @returns {Promise} - 请求Promise
   */
  detail(id) {
    return request.get(`${baseUrl}/detail`, { id })
  },

  /**
   * 获取相关公告
   * @param {Object} params - 请求参数
   * @param {string} params.id - 当前公告ID
   * @param {number} params.limit - 返回数量限制
   * @returns {Promise} - 请求Promise
   */
  related(params) {
    return request.get(`${baseUrl}/related`, params)
  },

  /**
   * 获取最新公告
   * @param {number} limit - 返回数量限制
   * @returns {Promise} - 请求Promise
   */
  latest(limit = 5) {
    return request.get(`${baseUrl}/latest`, { limit })
  }
}

export default notice 