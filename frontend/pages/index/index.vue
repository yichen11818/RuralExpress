<template>
  <view class="index-container">
    <!-- 骨架屏 -->
    <view class="skeleton-container" v-if="loading">
      <!-- 搜索骨架 -->
      <view class="search-skeleton"></view>
      
      <!-- 轮播图骨架 -->
      <view class="banner-skeleton"></view>
      
      <!-- 导航骨架 -->
      <view class="nav-skeleton">
        <view class="nav-item-skeleton" v-for="i in 4" :key="i"></view>
      </view>
      
      <!-- 公告骨架 -->
      <view class="notice-skeleton"></view>
      
      <!-- 快递员骨架 -->
      <view class="section-skeleton">
        <view class="title-skeleton"></view>
        <view class="courier-skeleton">
          <view class="courier-item-skeleton" v-for="i in 4" :key="i"></view>
        </view>
      </view>
      
      <!-- 订单骨架 -->
      <view class="section-skeleton">
        <view class="title-skeleton"></view>
        <view class="order-skeleton" v-for="i in 2" :key="i"></view>
      </view>
    </view>
    
    <!-- 实际内容 -->
    <block v-else>
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
          <text class="section-title">{{ userLocation ? '附近快递员' : '推荐快递员' }}</text>
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
      
      <!-- 最近订单 -->
      <view class="section-container" v-if="recentOrders.length > 0">
        <view class="section-header">
          <text class="section-title">最近订单</text>
          <text class="section-more" @click="navigateTo('/pages/order/order')">查看更多</text>
        </view>
        <view class="recent-orders">
          <view class="order-item" v-for="(item, index) in recentOrders" :key="index" @click="navigateTo(`/pages/order/detail?id=${item.id}`)">
            <view class="order-info">
              <view class="order-status" :class="'status-' + item.status">{{ getOrderStatusText(item.status) }}</view>
              <view class="order-time">{{ formatDate(item.createdAt) }}</view>
            </view>
            <view class="order-addresses">
              <view class="address-line">
                <text class="address-label">寄</text>
                <text class="address-value ellipsis">{{ item.senderAddress }}</text>
              </view>
              <view class="address-line">
                <text class="address-label">收</text>
                <text class="address-value ellipsis">{{ item.receiverAddress }}</text>
              </view>
            </view>
            <view class="order-action">
              <uni-icons type="right" size="16" color="#999"></uni-icons>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 价格计算器 -->
      <view class="section-container">
        <view class="section-header">
          <text class="section-title">快递费用计算</text>
        </view>
        <view class="calculator-container">
          <view class="calculator-form">
            <view class="form-item">
              <text class="label">包裹类型</text>
              <picker mode="selector" :range="packageTypes" @change="handlePackageTypeChange">
                <view class="picker-value">
                  <text>{{ packageTypes[selectedPackageType] }}</text>
                  <uni-icons type="arrowdown" size="14" color="#666"></uni-icons>
                </view>
              </picker>
            </view>
            <view class="form-item">
              <text class="label">预估距离</text>
              <view class="distance-slider">
                <slider :min="1" :max="20" :value="distance" :block-size="18" show-value @change="handleDistanceChange"></slider>
                <text class="distance-value">{{ distance }}公里</text>
              </view>
            </view>
          </view>
          <view class="price-result">
            <text class="price-label">预估费用:</text>
            <text class="price-value">¥{{ calculatedPrice.toFixed(2) }}</text>
          </view>
          <view class="calculator-actions">
            <button class="calc-btn" @click="navigateTo('/pages/delivery/send')">立即下单</button>
          </view>
        </view>
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
    </block>
  </view>
</template>

<script>
import { isLoggedIn } from '@/api/auth';
import { getHomeData, getNearestCouriers } from '@/api/home';
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
      nearestCouriers: [],
      userLocation: null,
      locationFailed: false,
      recentOrders: [],
      packageTypes: ['小件', '中件', '大件'],
      selectedPackageType: 0,
      distance: 10,
      calculatedPrice: 0
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
    
    // 尝试获取用户位置
    this.getUserLocation();
    
    // 计算初始价格
    this.calculatePrice();
  },
  
  onPullDownRefresh() {
    // 下拉刷新，重新获取位置和数据
    this.getUserLocation();
  },
  
  methods: {
    // 获取用户位置
    getUserLocation() {
      uni.showLoading({
        title: '定位中...'
      });
      
      uni.getLocation({
        type: 'wgs84',
        success: (res) => {
          const { latitude, longitude } = res;
          this.userLocation = { latitude, longitude };
          console.log('获取位置成功', this.userLocation);
          
          // 使用位置信息加载附近快递员
          this.loadNearbyCouriers();
          
          // 加载其他首页数据
          this.loadHomeData();
        },
        fail: (err) => {
          console.error('获取位置失败', err);
          this.locationFailed = true;
          
          uni.showToast({
            title: '获取位置信息失败，将显示推荐快递员',
            icon: 'none',
            duration: 2000
          });
          
          // 退回使用普通首页数据
          this.loadHomeData();
        },
        complete: () => {
          uni.hideLoading();
        }
      });
    },
    
    // 加载附近快递员
    loadNearbyCouriers() {
      if (!this.userLocation) return;
      
      const { latitude, longitude } = this.userLocation;
      getNearestCouriers(5, latitude, longitude)
        .then(res => {
          console.log('附近快递员响应:', res);
          if (res && res.code === 200 && res.data) {
            // 处理快递员数据
            this.nearestCouriers = res.data.map(courier => {
              return {
                id: courier.id,
                name: courier.name || courier.userName || '未知快递员',
                avatar: courier.avatar || '/static/images/default-avatar.png',
                rating: courier.rating || 5.0,
                completedOrders: courier.completedOrders || 0
              };
            });
          }
        })
        .catch(err => {
          console.error('获取附近快递员失败', err);
          // 如果获取附近快递员失败，不清空数据，保留之前的推荐快递员
        });
    },
    
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
            
            // 如果没有位置信息或获取附近快递员失败，使用推荐快递员
            if (!this.userLocation || this.nearestCouriers.length === 0) {
              this.nearestCouriers = (data.nearestCouriers || []).map(courier => {
                return {
                  id: courier.id,
                  name: courier.name || courier.userName || '未知快递员',
                  avatar: courier.avatar || '/static/images/default-avatar.png',
                  rating: courier.rating || 5.0,
                  completedOrders: courier.completedOrders || 0
                };
              });
            }
            
            // 获取最近订单
            this.recentOrders = data.recentOrders || [];
          } else {
            console.warn('首页数据返回格式不正确:', res);
            // 显示错误提示
            uni.showToast({
              title: '数据加载异常',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取首页数据失败', err);
          uni.showToast({
            title: '数据加载失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
          if (typeof callback === 'function') {
            callback();
          }
          uni.stopPullDownRefresh();
        });
    },
    
    // 清空数据
    clearData() {
      this.banners = [];
      this.notices = [];
      this.nearestCouriers = [];
      this.recentOrders = [];
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
    },
    
    // 获取订单状态文本
    getOrderStatusText(status) {
      const statusTexts = {
        'pending': '待发货',
        'shipped': '已发货',
        'delivered': '已送达',
        'cancelled': '已取消'
      };
      return statusTexts[status] || '未知状态';
    },
    
    // 格式化日期
    formatDate(dateStr) {
      const date = new Date(dateStr);
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    },
    
    // 处理包裹类型变化
    handlePackageTypeChange(e) {
      this.selectedPackageType = e.detail.value;
      this.calculatePrice();
    },
    
    // 处理距离变化
    handleDistanceChange(e) {
      this.distance = e.detail.value;
      this.calculatePrice();
    },
    
    // 计算价格
    calculatePrice() {
      // 基础价格
      const basePrice = 5;
      
      // 距离费用: 每公里1元，前2公里免费
      const distanceFee = Math.max(0, this.distance - 2) * 1;
      
      // 包裹类型附加费
      let packageTypeFee = 0;
      switch (parseInt(this.selectedPackageType)) {
        case 0: // 小件
          packageTypeFee = 0;
          break;
        case 1: // 中件
          packageTypeFee = 3;
          break;
        case 2: // 大件
          packageTypeFee = 6;
          break;
      }
      
      // 计算总价
      this.calculatedPrice = basePrice + distanceFee + packageTypeFee;
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
  width: 100%;
  white-space: nowrap;
  padding: 20rpx 0;
}

.courier-item {
  display: inline-block;
  width: 200rpx;
  margin-right: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  text-align: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.courier-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-bottom: 10rpx;
}

.courier-name {
  font-size: 28rpx;
  margin-bottom: 10rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.courier-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10rpx;
}

.rating-text {
  font-size: 24rpx;
  color: #ff9900;
  margin-left: 5rpx;
}

.courier-orders {
  font-size: 24rpx;
  color: #999;
}

.recent-orders {
  padding: 0 30rpx;
}

.order-item {
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  border-radius: 10rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.order-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 180rpx;
}

.order-status {
  font-size: 28rpx;
  font-weight: bold;
  color: #3cc51f;
}

.order-status.status-pending {
  color: #ff9900;
}

.order-status.status-cancelled {
  color: #ff3b30;
}

.order-time {
  font-size: 24rpx;
  color: #999;
}

.order-addresses {
  flex: 1;
  padding: 0 20rpx;
}

.address-line {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.address-label {
  width: 40rpx;
  height: 40rpx;
  line-height: 40rpx;
  text-align: center;
  border-radius: 20rpx;
  font-size: 24rpx;
  margin-right: 10rpx;
  color: #fff;
}

.address-line:first-child .address-label {
  background-color: #3cc51f;
}

.address-line:last-child .address-label {
  background-color: #ff9900;
}

.address-value {
  font-size: 28rpx;
  width: 380rpx;
}

.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.order-action {
  display: flex;
  align-items: center;
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

/* 骨架屏样式 */
.skeleton-container {
  padding-bottom: 40rpx;
}

.search-skeleton {
  height: 70rpx;
  margin: 20rpx 30rpx;
  background-color: #f0f0f0;
  border-radius: 35rpx;
  animation: skeleton-loading 1.5s infinite;
}

.banner-skeleton {
  height: 300rpx;
  background-color: #f0f0f0;
  animation: skeleton-loading 1.5s infinite;
}

.nav-skeleton {
  display: flex;
  justify-content: space-around;
  padding: 30rpx;
}

.nav-item-skeleton {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background-color: #f0f0f0;
  margin-bottom: 20rpx;
  animation: skeleton-loading 1.5s infinite;
}

.notice-skeleton {
  height: 60rpx;
  margin: 20rpx 30rpx;
  background-color: #f0f0f0;
  border-radius: 30rpx;
  animation: skeleton-loading 1.5s infinite;
}

.section-skeleton {
  margin: 30rpx 0;
}

.title-skeleton {
  width: 200rpx;
  height: 40rpx;
  margin: 20rpx 30rpx;
  background-color: #f0f0f0;
  border-radius: 4rpx;
  animation: skeleton-loading 1.5s infinite;
}

.courier-skeleton {
  display: flex;
  padding: 0 30rpx;
  overflow-x: scroll;
}

.courier-item-skeleton {
  width: 200rpx;
  height: 240rpx;
  margin-right: 20rpx;
  background-color: #f0f0f0;
  border-radius: 10rpx;
  animation: skeleton-loading 1.5s infinite;
}

.order-skeleton {
  height: 180rpx;
  margin: 20rpx 30rpx;
  background-color: #f0f0f0;
  border-radius: 10rpx;
  animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 0.8;
  }
  100% {
    opacity: 0.6;
  }
}

.calculator-container {
  padding: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.calculator-form {
  margin-bottom: 20rpx;
}

.form-item {
  margin-bottom: 20rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.picker-value {
  display: flex;
  align-items: center;
  background-color: #fff;
  height: 70rpx;
  border-radius: 35rpx;
  padding: 0 30rpx;
}

.distance-slider {
  display: flex;
  align-items: center;
  background-color: #fff;
  height: 70rpx;
  border-radius: 35rpx;
  padding: 0 30rpx;
}

.distance-value {
  font-size: 28rpx;
  color: #333;
  margin-left: 20rpx;
}

.price-result {
  margin-bottom: 20rpx;
}

.price-label {
  font-size: 28rpx;
  color: #333;
  margin-right: 10rpx;
}

.price-value {
  font-size: 28rpx;
  color: #3cc51f;
}

.calculator-actions {
  text-align: right;
}

.calc-btn {
  background-color: #3cc51f;
  color: #fff;
  padding: 10rpx 20rpx;
  border-radius: 35rpx;
  font-size: 28rpx;
}
</style> 