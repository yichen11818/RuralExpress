/**
 * 认证相关的工具函数
 */

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export function isLoggedIn() {
  try {
    const token = uni.getStorageSync('token');
    return !!token;
  } catch (e) {
    console.error('检查登录状态失败', e);
    return false;
  }
}

/**
 * 获取用户信息
 * @returns {object|null} 用户信息
 */
export function getUserInfo() {
  try {
    const userInfo = uni.getStorageSync('userInfo');
    return userInfo ? JSON.parse(userInfo) : null;
  } catch (e) {
    console.error('获取用户信息失败', e);
    return null;
  }
}

/**
 * 获取用户令牌
 * @returns {string} 用户令牌
 */
export function getToken() {
  try {
    return uni.getStorageSync('token') || '';
  } catch (e) {
    console.error('获取用户令牌失败', e);
    return '';
  }
}

/**
 * 保存登录状态
 * @param {string} token 令牌
 * @param {object} userInfo 用户信息
 * @returns {boolean} 是否保存成功
 */
export function saveLoginState(token, userInfo) {
  try {
    uni.setStorageSync('token', token);
    uni.setStorageSync('userInfo', JSON.stringify(userInfo));
    return true;
  } catch (e) {
    console.error('保存登录状态失败', e);
    return false;
  }
}

/**
 * 清除登录状态
 * @returns {boolean} 是否清除成功
 */
export function clearLoginState() {
  try {
    uni.removeStorageSync('token');
    uni.removeStorageSync('userInfo');
    return true;
  } catch (e) {
    console.error('清除登录状态失败', e);
    return false;
  }
}

/**
 * 验证登录状态，如果未登录则跳转到登录页面
 * @param {boolean} redirect 是否自动跳转到登录页
 * @returns {boolean} 是否已登录
 */
export function checkLogin(redirect = true) {
  const loggedIn = isLoggedIn();
  
  // 如果未登录且需要跳转，则跳转到登录页
  if (!loggedIn && redirect) {
    uni.redirectTo({
      url: '/pages/login/login'
    });
  }
  
  return loggedIn;
}

/**
 * 退出登录
 */
export function logout() {
  clearLoginState();
  uni.reLaunch({
    url: '/pages/login/login'
  });
} 