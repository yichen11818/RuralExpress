<template>
  <view class="settings-container">
    <!-- 增加顶部状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <view class="settings-header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="page-title">设置</text>
    </view>
    
    <view class="settings-list">
      <view class="settings-item" @click="navigateTo('/pages/user/profile')">
        <text class="settings-name">个人资料</text>
        <uni-icons type="right" size="16" color="#ccc"></uni-icons>
      </view>
      
      <view class="settings-item" @click="changePassword">
        <text class="settings-name">修改密码</text>
        <uni-icons type="right" size="16" color="#ccc"></uni-icons>
      </view>
      
      <view class="settings-item">
        <text class="settings-name">推送通知</text>
        <switch :checked="notifications" @change="toggleNotifications" color="#3cc51f" />
      </view>
      
      <view class="settings-item">
        <text class="settings-name">定位服务</text>
        <switch :checked="locationEnabled" @change="toggleLocation" color="#3cc51f" />
      </view>
      
      <view class="settings-item" @click="clearCache">
        <text class="settings-name">清除缓存</text>
        <uni-icons type="right" size="16" color="#ccc"></uni-icons>
      </view>
      
      <view class="settings-item" @click="navigateTo('/pages/about/about')">
        <text class="settings-name">关于我们</text>
        <uni-icons type="right" size="16" color="#ccc"></uni-icons>
      </view>
    </view>
    
    <view class="logout-btn" @click="handleLogout">退出登录</view>
  </view>
</template>

<script>
import { logout } from '@/api/auth';

export default {
  data() {
    return {
      notifications: true,
      locationEnabled: true,
      statusBarHeight: 20 // 默认值
    };
  },
  
  onLoad() {
    // 获取状态栏高度
    this.getStatusBarHeight();
  },
  
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      // 获取系统信息
      uni.getSystemInfo({
        success: (res) => {
          this.statusBarHeight = res.statusBarHeight || 20;
        }
      });
    },
    
    goBack() {
      uni.navigateBack();
    },
    
    navigateTo(url) {
      uni.navigateTo({
        url
      });
    },
    
    changePassword() {
      uni.navigateTo({
        url: '/pages/user/password'
      });
    },
    
    toggleNotifications(e) {
      this.notifications = e.detail.value;
      uni.showToast({
        title: this.notifications ? '已开启推送通知' : '已关闭推送通知',
        icon: 'none'
      });
    },
    
    toggleLocation(e) {
      this.locationEnabled = e.detail.value;
      uni.showToast({
        title: this.locationEnabled ? '已开启定位服务' : '已关闭定位服务',
        icon: 'none'
      });
    },
    
    clearCache() {
      uni.showModal({
        title: '提示',
        content: '确定要清除缓存吗？',
        success: (res) => {
          if (res.confirm) {
            // 清除缓存
            uni.clearStorageSync();
            uni.showToast({
              title: '缓存已清除',
              icon: 'success'
            });
          }
        }
      });
    },
    
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            logout();
            uni.reLaunch({
              url: '/pages/login/login'
            });
          }
        }
      });
    }
  }
};
</script>

<style>
.settings-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

/* 状态栏占位 */
.status-bar {
  width: 100%;
  background-color: #fff;
}

.settings-header {
  position: relative;
  padding: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  margin-bottom: 20rpx;
  /* 调整顶部和底部的padding更合理 */
  padding-top: 20rpx;
  padding-bottom: 20rpx;
}

.back-btn {
  position: absolute;
  left: 30rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.settings-list {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.settings-name {
  font-size: 30rpx;
  color: #333;
}

.logout-btn {
  width: 90%;
  height: 90rpx;
  line-height: 90rpx;
  text-align: center;
  background-color: #fff;
  color: #ff5a5f;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin: 60rpx auto;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  font-weight: bold;
}

.logout-btn:active {
  opacity: 0.8;
}
</style> 