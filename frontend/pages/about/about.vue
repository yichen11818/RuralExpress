<template>
  <view class="about-container">
    <!-- 增加顶部状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <view class="about-header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="page-title">关于我们</text>
    </view>
    
    <view class="logo-section">
      <!-- 由于logo.png可能不存在，使用default-avatar.png作为临时替代 -->
      <image class="logo" src="/static/images/default-avatar.png" mode="aspectFit"></image>
      <text class="app-name">乡递通</text>
      <text class="app-version">版本 1.0.0</text>
    </view>
    
    <view class="about-content">
      <view class="about-section">
        <text class="section-title">应用简介</text>
        <text class="section-text">乡递通是一款专注于乡村快递配送的应用，通过解决乡村快递配送最后一公里问题，为乡村居民提供便捷、高效的快递服务。</text>
      </view>
      
      <view class="about-section">
        <text class="section-title">我们的使命</text>
        <text class="section-text">通过创新的物流解决方案，连接城乡，缩小城乡物流服务差距，让每一位乡村居民都能享受到便捷的现代物流服务。</text>
      </view>
      
      <view class="about-section">
        <text class="section-title">联系我们</text>
        <view class="contact-item">
          <uni-icons type="phone" size="18" color="#3cc51f"></uni-icons>
          <text class="contact-text">客服电话：400-123-4567</text>
        </view>
        <view class="contact-item">
          <uni-icons type="email" size="18" color="#3cc51f"></uni-icons>
          <text class="contact-text">电子邮箱：support@ruralexpress.com</text>
        </view>
        <view class="contact-item">
          <uni-icons type="location" size="18" color="#3cc51f"></uni-icons>
          <text class="contact-text">公司地址：广东省深圳市南山区科技园1号</text>
        </view>
      </view>
    </view>
    
    <view class="copyright">© 2025 乡递通 版权所有</view>
  </view>
</template>

<script>
export default {
  data() {
    return {
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
    }
  }
};
</script>

<style>
.about-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

/* 状态栏占位 */
.status-bar {
  width: 100%;
  background-color: #fff;
}

.about-header {
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

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  background-color: #fff;
  margin-bottom: 20rpx;
  margin: 20rpx;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.logo {
  width: 150rpx;
  height: 150rpx;
  margin-bottom: 20rpx;
}

.app-name {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.app-version {
  font-size: 26rpx;
  color: #999;
}

.about-content {
  background-color: #fff;
  padding: 30rpx;
  margin: 20rpx;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.about-section {
  margin-bottom: 40rpx;
}

.about-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.section-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  display: block;
}

.contact-item {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.contact-item:last-child {
  margin-bottom: 0;
}

.contact-text {
  font-size: 28rpx;
  color: #666;
  margin-left: 20rpx;
}

.copyright {
  font-size: 24rpx;
  color: #999;
  text-align: center;
  padding: 30rpx 0;
}
</style> 