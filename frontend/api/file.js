/**
 * 文件上传相关的API服务
 */
import request from '@/utils/request';

/**
 * 上传文件
 * @param {File} file 文件对象
 * @returns {Promise} 上传结果
 */
export function uploadFile(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 上传图片
 * @param {File} file 图片文件
 * @returns {Promise} 上传结果
 */
export function uploadImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request.post('/file/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 删除文件
 * @param {String} fileUrl 文件URL
 * @returns {Promise} 删除结果
 */
export function deleteFile(fileUrl) {
  return request.delete(`/file/delete?url=${encodeURIComponent(fileUrl)}`);
}

/**
 * 获取文件预览URL
 * @param {String} fileUrl 文件URL
 * @returns {String} 预览URL
 */
export function getPreviewUrl(fileUrl) {
  if (!fileUrl) return '';
  return `${process.env.VUE_APP_BASE_API}/file/preview?url=${encodeURIComponent(fileUrl)}`;
} 