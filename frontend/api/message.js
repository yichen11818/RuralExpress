import request from '@/utils/request';
import { getToken } from '@/utils/auth';

// 获取用户未读消息数量
export function getUnreadMessageCount() {
  console.log('调用获取未读消息数量API');
  return request.get('/messages/unread/count')
    .then(res => {
      console.log('获取未读消息数量成功:', res);
      return res;
    })
    .catch(err => {
      console.error('获取未读消息数量失败', err);
      // 返回一个默认值为0的结果，避免页面错误
      return { code: 200, data: 0 };
    });
}

// 获取用户消息列表
export function getUserMessages(page = 1, pageSize = 10, type = null) {
  const params = { page, pageSize };
  if (type !== null) {
    params.type = type;
  }
  return request.get('/messages/list', params);
}

// 标记消息为已读
export function markMessageAsRead(messageId) {
  return request.put(`/messages/${messageId}/read`);
}

// 获取管理员未处理的快递员申请消息
export function getPendingCourierApplications(page = 1, pageSize = 10) {
  const params = { page, pageSize };
  return request.get('/messages/courier-applications', params);
} 