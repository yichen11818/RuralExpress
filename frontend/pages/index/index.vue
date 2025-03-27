<template>
  <view class="index-container">
    <!-- 头部搜索区域 -->
    <view class="search-container">
      <view class="search-box" @click="navigateTo('/pages/search/search')">
        <uni-icons type="search" size="18" color="#666"></uni-icons>
        <text class="search-placeholder">搜索订单/快递单号</text>
      </view>
    </view>
    
    <!-- 轮播图区域 -->
    <swiper class="banner-swiper" indicator-dots autoplay interval="3000" duration="500" circular>
      <swiper-item v-for="(item, index) in banners" :key="index" @click="handleBannerClick(item)">
        <image :src="item.imageUrl" mode="aspectFill" class="banner-image"></image>
      </swiper-item>
    </swiper>
    
    <!-- 功能导航区域 -->
    <view class="nav-container">
      <view class="nav-item" @click="navigateTo('/pages/delivery/send')">
        <image src="/static/images/send.png" mode="aspectFit" class="nav-image"></image>
        <text class="nav-text">寄件</text>
      </view>
      <view class="nav-item" @click="navigateTo('/pages/delivery/receive')">
        <image src="/static/images/receive.png" mode="aspectFit" class="nav-image"></image>
        <text class="nav-text">收件</text>
      </view>
      <view class="nav-item" @click="navigateTo('/pages/order/track')">
        <image src="/static/images/track.png" mode="aspectFit" class="nav-image"></image>
        <text class="nav-text">物流跟踪</text>
      </view>
      <view class="nav-item" @click="navigateTo('/pages/courier/recruitment')">
        <image src="/static/images/recruit.png" mode="aspectFit" class="nav-image"></image>
        <text class="nav-text">招募快递员</text>
      </view>
    </view>
    
    <!-- 公告区域 -->
    <view class="notice-container">
      <uni-icons type="notification" size="18" color="#3cc51f"></uni-icons>
      <swiper class="notice-swiper" vertical autoplay circular interval="3000" duration="500">
        <swiper-item v-for="(item, index) in notices" :key="index" @click="handleNoticeClick(item)">
          <text class="notice-text">{{ item.content }}</text>
        </swiper-item>
      </swiper>
    </view>
    
    <!-- 推荐快递员 -->
    <view class="section-container" v-if="nearestCouriers.length > 0">
      <view class="section-header">
        <text class="section-title">周边快递员</text>
        <text class="section-more" @click="navigateTo('/pages/courier/list')">查看更多</text>
      </view>
      <scroll-view scroll-x class="courier-scroll">
        <view class="courier-item" v-for="(item, index) in nearestCouriers" :key="index" @click="navigateTo(`/pages/courier/detail?id=${item.id}`)">
          <image :src="item.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="courier-avatar"></image>
          <text class="courier-name">{{ item.name }}</text>
          <view class="courier-rating">
            <uni-icons type="star-filled" size="12" color="#ff9900"></uni-icons>
            <text class="rating-text">{{ item.rating }}</text>
          </view>
          <text class="courier-orders">已完成{{ item.completedOrders }}单</text>
        </view>
      </scroll-view>
    </view>
    
    <!-- 服务保障 -->
    <view class="guarantee-container">
      <view class="guarantee-item">
        <uni-icons type="checkmarkempty" size="20" color="#3cc51f"></uni-icons>
        <text class="guarantee-text">快递安全保障</text>
      </view>
      <view class="guarantee-item">
        <uni-icons type="checkmarkempty" size="20" color="#3cc51f"></uni-icons>
        <text class="guarantee-text">专业物流配送</text>
      </view>
      <view class="guarantee-item">
        <uni-icons type="checkmarkempty" size="20" color="#3cc51f"></uni-icons>
        <text class="guarantee-text">7*24小时服务</text>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view class="loading-container" v-if="loading">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script>
import { isLoggedIn } from '@/api/auth';
import { getHomeData } from '@/api/home';
import uniIcons from '../../uni_modules/uni-icons/components/uni-icons/uni-icons.vue'

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      loading: true,
      banners: [],
      notices: [],
      nearestCouriers: []
    };
  },
  
  onLoad() {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 加载首页数据
    this.loadHomeData();
  },
  
  onPullDownRefresh() {
    // 下拉刷新，重新加载数据
    this.loadHomeData(() => {
      uni.stopPullDownRefresh();
      uni.showToast({
        title: '刷新成功',
        icon: 'success'
      });
    });
  },
  
  methods: {
    // 加载首页数据
    loadHomeData(callback) {
      this.loading = true;
      
      getHomeData()
        .then(res => {
          console.log('首页数据响应:', res);
          if (res && res.code === 200 && res.data) {
            const data = res.data;
            this.banners = data.banners || [];
            this.notices = data.notices || [];
            this.nearestCouriers = data.nearestCouriers || [];
            
            // 处理快递员数据，保证字段一致性
            this.nearestCouriers = this.nearestCouriers.map(courier => {
              return {
                id: courier.id,
                name: courier.name || courier.userName || '未知快递员',
                avatar: courier.avatar || '/static/images/default-avatar.png',
                rating: courier.rating || 5.0,
                completedOrders: courier.completedOrders || 0
              };
            });
          } else {
            console.warn('首页数据返回格式不正确:', res);
            // 显示错误提示
            uni.showToast({
              title: '数据加载异常',
              icon: 'none'
            });
            // 清空数据
            this.clearData();
          }
        })
        .catch(err => {
          console.error('获取首页数据失败', err);
          uni.showToast({
            title: '数据加载失败',
            icon: 'none'
          });
          
          // 清空数据
          this.clearData();
        })
        .finally(() => {
          this.loading = false;
          callback && callback();
        });
    },
    
    // 清空数据
    clearData() {
      this.banners = [];
      this.notices = [];
      this.nearestCouriers = [];
    },
    
    // 页面导航
    navigateTo(url) {
      uni.navigateTo({
        url
      });
    },
    
    // 处理轮播图点击
    handleBannerClick(banner) {
      if (banner.linkUrl) {
        this.navigateTo(banner.linkUrl);
      }
    },
    
    // 处理公告点击
    handleNoticeClick(notice) {
      if (notice.linkUrl) {
        this.navigateTo(notice.linkUrl);
      }
    }
  }
};
</script>

<style>
.index-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.search-container {
  padding: 20rpx 30rpx;
  background-color: #3cc51f;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #fff;
  height: 70rpx;
  border-radius: 35rpx;
  padding: 0 30rpx;
}

.search-placeholder {
  font-size: 28rpx;
  color: #999;
  margin-left: 10rpx;
}

.banner-swiper {
  width: 100%;
  height: 300rpx;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.nav-container {
  display: flex;
  background-color: #fff;
  padding: 30rpx 20rpx;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  margin: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.nav-image {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 10rpx;
  background-color: #3cc51f;
  border-radius: 50%;
  padding: 16rpx;
  box-sizing: border-box;
  transition: transform 0.3s ease;
  box-shadow: 0 6rpx 12rpx rgba(0, 0, 0, 0.1);
}

.nav-item:nth-child(1) .nav-image {
  background-color: #3cc51f;
}

.nav-item:nth-child(2) .nav-image {
  background-color: #1296db;
}

.nav-item:nth-child(3) .nav-image {
  background-color: #ff9900;
}

.nav-item:nth-child(4) .nav-image {
  background-color: #e64340;
}

.nav-item:active .nav-image {
  transform: scale(0.9);
}

.nav-text {
  font-size: 28rpx;
  color: #333;
  margin-top: 6rpx;
}

.notice-container {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 20rpx 30rpx;
  margin-bottom: 20rpx;
}

.notice-swiper {
  flex: 1;
  height: 40rpx;
  margin-left: 20rpx;
}

.notice-text {
  font-size: 26rpx;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.section-container {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.section-more {
  font-size: 26rpx;
  color: #666;
}

.courier-scroll {
  white-space: nowrap;
}

.courier-item {
  display: inline-block;
  width: 180rpx;
  margin-right: 30rpx;
}

.courier-avatar {
  width: 180rpx;
  height: 180rpx;
  border-radius: 10rpx;
  margin-bottom: 10rpx;
  background-color: #f0f0f0;
}

.courier-name {
  font-size: 28rpx;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.courier-rating {
  display: flex;
  align-items: center;
  margin: 6rpx 0;
}

.rating-text {
  font-size: 24rpx;
  color: #ff9900;
  margin-left: 6rpx;
}

.courier-orders {
  font-size: 24rpx;
  color: #999;
}

.guarantee-container {
  background-color: #fff;
  display: flex;
  padding: 20rpx 0;
  justify-content: space-around;
}

.guarantee-item {
  display: flex;
  align-items: center;
}

.guarantee-text {
  font-size: 24rpx;
  color: #666;
  margin-left: 6rpx;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30rpx;
}

.loading-spinner {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid rgba(0, 0, 0, 0.1);
  border-left-color: #3cc51f;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style> 