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
      <view class="header-container">
        <view class="search-container">
          <view class="search-box" @click="navigateTo('/pages/search/index')">
            <uni-icons type="search" size="18" color="#999"></uni-icons>
            <text class="search-placeholder">搜索订单/快递单号</text>
          </view>
        </view>
      </view>
      
      <!-- 轮播图区域 -->
      <swiper class="banner-swiper" indicator-dots autoplay interval="3000" duration="500" circular indicator-color="rgba(255,255,255,0.6)" indicator-active-color="#FF6B35">
        <swiper-item v-for="(item, index) in banners" :key="index" @click="handleBannerClick(item)">
          <image :src="item.imageUrl" mode="aspectFill" class="banner-image"></image>
        </swiper-item>
      </swiper>
      
      <!-- 功能导航区域 -->
      <view class="nav-container">
        <view class="nav-item" @click="navigateTo('/pages/delivery/send')">
          <view class="nav-icon-wrapper nav-send">
            <image src="/static/images/send.png" mode="aspectFit" class="nav-image"></image>
          </view>
          <text class="nav-text">寄件</text>
        </view>
        <view class="nav-item" @click="navigateTo('/pages/delivery/receive')">
          <view class="nav-icon-wrapper nav-receive">
            <image src="/static/images/receive.png" mode="aspectFit" class="nav-image"></image>
          </view>
          <text class="nav-text">收件</text>
        </view>
        <view class="nav-item" @click="navigateTo('/pages/order/tracking-list')">
          <view class="nav-icon-wrapper nav-track">
            <image src="/static/images/track.png" mode="aspectFit" class="nav-image"></image>
          </view>
          <text class="nav-text">物流跟踪</text>
        </view>
        <view class="nav-item" @click="navigateTo('/pages/courier/recruitment')">
          <view class="nav-icon-wrapper nav-recruit">
            <image src="/static/images/recruit.png" mode="aspectFit" class="nav-image"></image>
          </view>
          <text class="nav-text">招募快递员</text>
        </view>
      </view>
      
      <!-- 公告区域 -->
      <view class="notice-container">
        <view class="notice-icon">
          <uni-icons type="notification-filled" size="18" color="#FF6B35"></uni-icons>
        </view>
        <swiper class="notice-swiper" vertical autoplay circular interval="3000" duration="500">
          <swiper-item v-for="(item, index) in notices" :key="index" @click="handleNoticeClick(item)">
            <text class="notice-text">{{ item.content }}</text>
          </swiper-item>
        </swiper>
      </view>
      
      <!-- 物流追踪列表 -->
      <view class="section-container" v-if="trackingList.length > 0">
        <view class="section-header">
          <view class="section-title-wrapper">
            <view class="section-title-mark"></view>
            <text class="section-title">物流追踪</text>
          </view>
          <view class="section-more" @click="navigateTo('/pages/order/tracking-list')">
            <text>查看更多</text>
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
        <view class="tracking-list">
          <view class="tracking-item" v-for="(item, index) in trackingList" :key="index" @click="navigateTo(`/pages/order/track?trackingNo=${item.trackingNo}`)">
            <view class="tracking-company">
              <image :src="item.logo || '/static/images/package.png'" mode="aspectFit" class="tracking-logo"></image>
              <view class="tracking-info">
                <text class="tracking-name">{{ item.company }}</text>
                <text class="tracking-number">{{ item.trackingNo }}</text>
              </view>
            </view>
            <view class="tracking-status">
              <text class="status-text" :class="'status-' + item.status">{{ getTrackingStatusText(item.status) }}</text>
              <uni-icons type="right" size="16" color="#C8C8C8"></uni-icons>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 推荐快递员 -->
      <view class="section-container" v-if="nearestCouriers.length > 0">
        <view class="section-header">
          <view class="section-title-wrapper">
            <view class="section-title-mark"></view>
            <text class="section-title">{{ userLocation ? '附近快递员' : '推荐快递员' }}</text>
          </view>
          <view class="section-more" @click="navigateTo('/pages/courier/list')">
            <text>查看更多</text>
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
        <scroll-view scroll-x class="courier-scroll" show-scrollbar="false">
          <view class="courier-item" v-for="(item, index) in nearestCouriers" :key="index" @click="navigateTo(`/pages/courier/detail?id=${item.id}`)">
            <image :src="item.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="courier-avatar"></image>
            <text class="courier-name">{{ item.name || '快递员' + (index + 1) }}</text>
            <view class="courier-info">
              <view class="courier-rating">
                <uni-icons type="star-filled" size="12" color="#FFAC33"></uni-icons>
                <text class="rating-text">{{ (item.rating || 5.0).toFixed(1) }}</text>
              </view>
              <view class="courier-orders">已完成{{ item.completedOrders || 0 }}单</view>
            </view>
            <text v-if="item.distance" class="courier-distance">
              <uni-icons type="location" size="12" color="#999"></uni-icons>
              {{ item.distance }}
            </text>
          </view>
        </scroll-view>
      </view>
      
      <!-- 最近订单 -->
      <view class="section-container" v-if="recentOrders.length > 0">
        <view class="section-header">
          <view class="section-title-wrapper">
            <view class="section-title-mark"></view>
            <text class="section-title">最近订单</text>
          </view>
          <view class="section-more" @click="navigateTo('/pages/order/order')">
            <text>查看更多</text>
            <uni-icons type="right" size="14" color="#999"></uni-icons>
          </view>
        </view>
        <view class="recent-orders">
          <view class="order-item" v-for="(item, index) in recentOrders" :key="index" @click="navigateTo(`/pages/order/detail?id=${item.id}`)">
            <view class="order-left">
              <view class="order-status" :class="'status-' + item.status">{{ getOrderStatusText(item.status) }}</view>
              <view class="order-time">{{ formatDate(item.createdAt) }}</view>
            </view>
            <view class="order-addresses">
              <view class="address-line">
                <text class="address-label sender">寄</text>
                <text class="address-value ellipsis">{{ item.senderAddress }}</text>
              </view>
              <view class="address-route">
                <view class="route-line"></view>
                <uni-icons type="arrowdown" size="14" color="#ddd"></uni-icons>
              </view>
              <view class="address-line">
                <text class="address-label receiver">收</text>
                <text class="address-value ellipsis">{{ item.receiverAddress }}</text>
              </view>
            </view>
            <view class="order-arrow">
              <uni-icons type="right" size="16" color="#C8C8C8"></uni-icons>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 价格计算器 -->
      <view class="section-container calc-section">
        <view class="section-header">
          <view class="section-title-wrapper">
            <view class="section-title-mark"></view>
            <text class="section-title">快递费用计算</text>
          </view>
        </view>
        <view class="calculator-container">
          <view class="calculator-form">
            <view class="form-item">
              <text class="label">包裹类型</text>
              <picker mode="selector" :range="packageTypes" @change="handlePackageTypeChange" class="picker">
                <view class="picker-value">
                  <text>{{ packageTypes[selectedPackageType] }}</text>
                  <uni-icons type="arrowdown" size="14" color="#999"></uni-icons>
                </view>
              </picker>
            </view>
            <view class="form-item">
              <text class="label">预估距离</text>
              <view class="distance-slider">
                <slider :min="1" :max="20" :value="distance" :block-size="20" show-value @change="handleDistanceChange" active-color="#FF6B35" block-color="#FF6B35"></slider>
                <text class="distance-value">{{ distance }}公里</text>
              </view>
            </view>
          </view>
          <view class="calculator-result">
            <view class="price-result">
              <text class="price-label">预估费用</text>
              <text class="price-value">¥{{ calculatedPrice.toFixed(2) }}</text>
            </view>
            <button class="calc-btn" @click="navigateTo('/pages/delivery/send')">立即下单</button>
          </view>
        </view>
      </view>
      
      <!-- 服务保障 -->
      <view class="guarantee-container">
        <view class="guarantee-item">
          <uni-icons type="checkmarkempty" size="16" color="#FF6B35"></uni-icons>
          <text class="guarantee-text">快递安全保障</text>
        </view>
        <view class="guarantee-item">
          <uni-icons type="checkmarkempty" size="16" color="#FF6B35"></uni-icons>
          <text class="guarantee-text">专业物流配送</text>
        </view>
        <view class="guarantee-item">
          <uni-icons type="checkmarkempty" size="16" color="#FF6B35"></uni-icons>
          <text class="guarantee-text">7*24小时服务</text>
        </view>
      </view>
    </block>
  </view>
</template>

<script>
import { isLoggedIn } from '@/api/auth';
import { getHomeData, getNearestCouriers } from '@/api/home';
import { getTrackingList } from '@/api/order';
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
      trackingList: [],
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
    // 清除首页加载标记，以便重新显示loading
    uni.removeStorageSync('_home_loaded_');
    
    // 是否重新获取位置 - 通常下拉刷新不需要重新定位，以避免频繁请求位置权限
    // 而是重新加载数据即可
    if (this.userLocation && !this.locationFailed) {
      // 如果已有位置信息，直接刷新数据
      this.loadNearbyCouriers();
      this.loadHomeData(() => {
        uni.showToast({
          title: '刷新成功',
          icon: 'success',
          duration: 1500
        });
      });
    } else {
      // 如果没有位置信息或获取位置失败过，尝试重新获取位置
      this.getUserLocation();
    }
  },
  
  methods: {
    // 获取用户位置
    getUserLocation() {
      // 标记是否已关闭loading
      let loadingClosed = false;
      
      // 先检查位置权限
      uni.getSetting({
        success: (res) => {
          // 判断是否已经授权位置权限
          if (res.authSetting['scope.userLocation']) {
            // 已授权，显示loading
            uni.showLoading({
              title: '定位中...'
            });
            
            // 设置一个超时处理，确保loading不会一直显示
            const timeout = setTimeout(() => {
              if (!loadingClosed) {
                loadingClosed = true;
                uni.hideLoading();
              }
            }, 10000); // 10秒超时
            
            // 获取位置
            this.getLocationInfo(loadingClosed, timeout);
          } else {
            // 未授权，先显示申请权限的提示
            uni.showModal({
              title: '位置信息',
              content: '为了向您提供附近的快递员服务，我们需要获取您的位置信息',
              success: (modalRes) => {
                if (modalRes.confirm) {
                  // 用户同意，申请权限
                  this.requestLocationPermission(loadingClosed);
                } else {
                  // 用户拒绝，提示并使用推荐快递员
                  this.handleLocationFailed('您拒绝了位置授权，将显示推荐快递员');
                }
              }
            });
          }
        },
        fail: () => {
          // 获取设置信息失败
          this.handleLocationFailed('获取权限信息失败，将显示推荐快递员');
        }
      });
    },
    
    // 申请位置权限
    requestLocationPermission(loadingClosed) {
      uni.authorize({
        scope: 'scope.userLocation',
        success: () => {
          // 授权成功，显示loading
          uni.showLoading({
            title: '定位中...'
          });
          
          // 设置一个超时处理
          const timeout = setTimeout(() => {
            if (!loadingClosed) {
              loadingClosed = true;
              uni.hideLoading();
            }
          }, 10000); // 10秒超时
          
          // 获取位置
          this.getLocationInfo(loadingClosed, timeout);
        },
        fail: () => {
          // 授权失败，提示用户在设置中开启
          uni.showModal({
            title: '提示',
            content: '获取位置权限失败，您可以在系统设置中开启位置权限',
            confirmText: '去设置',
            cancelText: '取消',
            success: (modalRes) => {
              if (modalRes.confirm) {
                // 打开设置页
                uni.openSetting({
                  success: (settingRes) => {
                    if (settingRes.authSetting['scope.userLocation']) {
                      // 用户在设置页开启了权限
                      uni.showLoading({
                        title: '定位中...'
                      });
                      
                      const timeout = setTimeout(() => {
                        if (!loadingClosed) {
                          loadingClosed = true;
                          uni.hideLoading();
                        }
                      }, 10000);
                      
                      this.getLocationInfo(loadingClosed, timeout);
                    } else {
                      // 用户仍然拒绝授权
                      this.handleLocationFailed('您拒绝了位置授权，将显示推荐快递员');
                    }
                  }
                });
              } else {
                // 用户取消，使用推荐快递员
                this.handleLocationFailed('您拒绝了位置授权，将显示推荐快递员');
              }
            }
          });
        }
      });
    },
    
    // 获取位置信息
    getLocationInfo(loadingClosed, timeout) {
      uni.getLocation({
        type: 'wgs84',
        success: (res) => {
          console.log('获取位置成功', res);
          const { latitude, longitude } = res;
          this.userLocation = { latitude, longitude };
          
          // 使用位置信息加载附近快递员
          this.loadNearbyCouriers();
          
          // 加载其他首页数据
          this.loadHomeData();
        },
        fail: (err) => {
          console.error('获取位置失败', err);
          this.handleLocationFailed();
        },
        complete: () => {
          // 防止重复关闭loading
          if (!loadingClosed) {
            loadingClosed = true;
            uni.hideLoading();
            clearTimeout(timeout);
          }
        }
      });
    },
    
    // 处理位置获取失败
    handleLocationFailed(message) {
      console.error('位置获取失败');
      this.locationFailed = true;
      
      // 显示提示
      if (message) {
        uni.showToast({
          title: message,
          icon: 'none',
          duration: 3000
        });
      } else {
        uni.showToast({
          title: '获取位置信息失败，将显示推荐快递员',
          icon: 'none',
          duration: 2000
        });
      }
      
      // 退回使用普通首页数据
      this.loadHomeData();
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
              // 根据快递员userId生成名称和头像
              const courierNames = {
                1: '张师傅',
                2: '李师傅',
                3: '王师傅',
                4: '刘师傅',
                5: '赵师傅'
              };
              
              const courierAvatars = {
                1: '/static/images/courier-1.png',
                2: '/static/images/courier-2.png',
                3: '/static/images/courier-3.png',
                4: '/static/images/courier-4.png',
                5: '/static/images/courier-5.png'
              };
              
              return {
                id: courier.id,
                name: courierNames[courier.userId] || `快递员${courier.userId}`, 
                avatar: courierAvatars[courier.userId] || '/static/images/default-avatar.png',
                rating: parseFloat(courier.rating) || 5.0,
                completedOrders: courier.completedOrders || 0,
                // 添加距离信息
                distance: courier.distance ? `${courier.distance}公里` : '未知距离'
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
      // 设置加载状态
      this.loading = true;
      
      // 避免重复显示loading
      const showLoading = !uni.getStorageSync('_home_loaded_');
      if (showLoading) {
        uni.showLoading({
          title: '加载数据中...'
        });
      }
      
      // 加载物流追踪列表
      this.loadTrackingList();
      
      getHomeData()
        .then(res => {
          console.log('首页数据响应:', res);
          if (res && res.code === 200 && res.data) {
            const data = res.data;
            this.banners = data.banners || [];
            this.notices = data.notices || [];
            
            // 如果没有位置信息或获取附近快递员失败，使用推荐快递员
            if (!this.userLocation || this.nearestCouriers.length === 0) {
              // 直接根据API返回数据结构映射快递员信息
              this.nearestCouriers = (data.nearestCouriers || []).map(courier => {
                // 根据userId映射到名称和头像
                const courierNames = {
                  1: '张师傅',
                  2: '李师傅',
                  3: '王师傅',
                  4: '刘师傅',
                  5: '赵师傅'
                };
                
                const courierAvatars = {
                  1: '/static/images/courier1.jpg',
                  2: '/static/images/courier2.jpg',
                  3: '/static/images/courier3.jpg',
                  4: '/static/images/courier4.jpg',
                  5: '/static/images/courier5.jpg'
                };
                
                return {
                  id: courier.id,
                  name: courierNames[courier.userId] || `快递员${courier.userId}`,
                  avatar: courierAvatars[courier.userId] || '/static/images/default-avatar.png',
                  rating: parseFloat(courier.rating) || 5.0,
                  completedOrders: courier.completedOrders || 0
                };
              });
            }
            
            // 获取最近订单
            this.recentOrders = data.recentOrders || [];
            
            // 标记首页已加载，避免重复显示loading
            uni.setStorageSync('_home_loaded_', true);
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
          // 关闭loading
          if (showLoading) {
            uni.hideLoading();
          }
          
          this.loading = false;
          
          if (typeof callback === 'function') {
            callback();
          }
          
          uni.stopPullDownRefresh();
        });
    },
    
    // 加载物流追踪列表
    loadTrackingList() {
      getTrackingList()
        .then(res => {
          console.log('物流追踪列表响应:', res);
          if (res && res.code === 200 && res.data) {
            // 修复：正确访问data.list数组
            const trackingData = res.data.list || [];
            // 只显示最近的3个物流信息
            this.trackingList = trackingData.slice(0, 3).map(item => {
              return {
                trackingNo: item.trackingNo,
                company: item.companyName || '未知快递公司',
                logo: item.companyLogo || '/static/images/package.png',
                status: item.status || 0
              };
            });
          }
        })
        .catch(err => {
          console.error('获取物流追踪列表失败', err);
        });
    },
    
    // 获取物流状态文本
    getTrackingStatusText(status) {
      const statusMap = {
        0: '等待揽收',
        1: '已揽收',
        2: '运输中',
        3: '已到达',
        4: '派送中',
        5: '已签收'
      };
      return statusMap[status] || '未知状态';
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
      // 检查公告是否有ID，如果有则跳转到详情页
      if (notice.id) {
        this.navigateTo(`/pages/notice/detail?id=${notice.id}`);
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
  background-color: #F6F6F6;
  padding-bottom: 40rpx;
}

/* 头部样式 */
.header-container {
  background: linear-gradient(to right, #FF6B35, #FF9A5A);
  padding: 20rpx 30rpx 30rpx;
  border-radius: 0 0 30rpx 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.15);
}

.search-container {
  position: relative;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #fff;
  height: 80rpx;
  border-radius: 40rpx;
  padding: 0 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.search-placeholder {
  font-size: 28rpx;
  color: #999;
  margin-left: 10rpx;
}

/* 轮播图样式 */
.banner-swiper {
  width: 100%;
  height: 340rpx;
  margin-top: -20rpx;
}

.banner-image {
  width: 100%;
  height: 100%;
  border-radius: 20rpx;
  margin: 0 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

/* 导航样式 */
.nav-container {
  display: flex;
  background-color: #fff;
  padding: 40rpx 20rpx;
  margin: 30rpx 20rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.nav-icon-wrapper {
  width: 100rpx;
  height: 100rpx;
  border-radius: 28rpx;
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.nav-icon-wrapper::after {
  content: '';
  position: absolute;
  width: 120%;
  height: 120%;
  background: linear-gradient(45deg, rgba(255,255,255,0.1), rgba(255,255,255,0.3));
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.nav-item:active .nav-icon-wrapper::after {
  transform: translateY(0);
}

.nav-send {
  background: linear-gradient(135deg, #FF6B35, #FF9A5A);
}

.nav-receive {
  background: linear-gradient(135deg, #4D96FF, #6EAEFF);
}

.nav-track {
  background: linear-gradient(135deg, #FFAC33, #FFD280);
}

.nav-recruit {
  background: linear-gradient(135deg, #7A42F4, #B388FF);
}

.nav-image {
  width: 50rpx;
  height: 50rpx;
  z-index: 1;
}

.nav-text {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
}

/* 公告样式 */
.notice-container {
  display: flex;
  align-items: center;
  background-color: #FFF7F2;
  padding: 16rpx 30rpx;
  margin: 0 20rpx 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(255, 107, 53, 0.1);
  border-left: 8rpx solid #FF6B35;
}

.notice-icon {
  background-color: rgba(255, 107, 53, 0.1);
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.notice-swiper {
  flex: 1;
  height: 60rpx;
}

.notice-text {
  font-size: 26rpx;
  color: #666;
  line-height: 60rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 板块通用样式 */
.section-container {
  background-color: #fff;
  margin: 0 20rpx 30rpx;
  padding: 30rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.section-title-wrapper {
  display: flex;
  align-items: center;
}

.section-title-mark {
  width: 8rpx;
  height: 36rpx;
  background-color: #FF6B35;
  border-radius: 4rpx;
  margin-right: 16rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.section-more {
  display: flex;
  align-items: center;
  color: #999;
  font-size: 26rpx;
}

/* 快递员模块样式 */
.courier-scroll {
  width: 100%;
  white-space: nowrap;
  padding: 10rpx 0;
}

.courier-item {
  display: inline-block;
  width: 200rpx;
  margin-right: 24rpx;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  text-align: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  vertical-align: top;
}

.courier-item:active {
  transform: scale(0.96);
}

.courier-avatar {
  width: 130rpx;
  height: 130rpx;
  border-radius: 65rpx;
  margin-bottom: 16rpx;
  border: 4rpx solid #F6F6F6;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.courier-name {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  margin-bottom: 12rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  text-align: center;
  color: #333;
  height: 40rpx;
  line-height: 40rpx;
}

.courier-info {
  margin-bottom: 10rpx;
}

.courier-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8rpx;
}

.rating-text {
  font-size: 24rpx;
  color: #FFAC33;
  margin-left: 6rpx;
}

.courier-orders {
  font-size: 22rpx;
  color: #999;
}

.courier-distance {
  font-size: 22rpx;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 订单模块样式 */
.recent-orders {
  padding: 0;
}

.order-item {
  display: flex;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx 0;
  margin-bottom: 24rpx;
  border-bottom: 2rpx solid #F6F6F6;
  position: relative;
}

.order-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.order-left {
  width: 180rpx;
  padding: 0 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.order-status {
  font-size: 28rpx;
  font-weight: bold;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  display: inline-block;
}

.status-pending {
  color: #FFAC33;
  background-color: rgba(255, 172, 51, 0.1);
}

.status-shipped {
  color: #4D96FF;
  background-color: rgba(77, 150, 255, 0.1);
}

.status-delivered {
  color: #3CC75A;
  background-color: rgba(60, 199, 90, 0.1);
}

.status-cancelled {
  color: #FF5151;
  background-color: rgba(255, 81, 81, 0.1);
}

.order-time {
  font-size: 22rpx;
  color: #999;
  margin-top: 16rpx;
}

.order-addresses {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0 16rpx;
}

.address-line {
  display: flex;
  align-items: center;
  margin-bottom: 6rpx;
}

.address-label {
  width: 36rpx;
  height: 36rpx;
  line-height: 36rpx;
  text-align: center;
  border-radius: 8rpx;
  font-size: 22rpx;
  margin-right: 16rpx;
  color: #fff;
}

.address-label.sender {
  background-color: #FF6B35;
}

.address-label.receiver {
  background-color: #4D96FF;
}

.address-route {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-left: 18rpx;
  height: 30rpx;
  margin-bottom: 6rpx;
}

.route-line {
  width: 2rpx;
  height: 16rpx;
  background-color: #ddd;
}

.address-value {
  font-size: 26rpx;
  color: #333;
  width: 360rpx;
}

.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.order-arrow {
  padding: 0 20rpx;
  display: flex;
  align-items: center;
}

/* 计算器样式 */
.calc-section {
  background: linear-gradient(to bottom, #fff, #FFF7F2);
}

.calculator-container {
  padding: 10rpx 0;
}

.calculator-form {
  margin-bottom: 30rpx;
}

.form-item {
  margin-bottom: 24rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
}

.picker {
  width: 100%;
}

.picker-value {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #F8F8F8;
  height: 80rpx;
  border-radius: 12rpx;
  padding: 0 30rpx;
}

.distance-slider {
  background-color: #F8F8F8;
  border-radius: 12rpx;
  padding: 20rpx 30rpx;
}

.distance-value {
  font-size: 28rpx;
  color: #333;
  margin-top: 16rpx;
  display: block;
  text-align: right;
}

.calculator-result {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 40rpx;
}

.price-result {
  display: flex;
  align-items: baseline;
}

.price-label {
  font-size: 28rpx;
  color: #666;
  margin-right: 16rpx;
}

.price-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #FF6B35;
}

.calc-btn {
  background: linear-gradient(to right, #FF6B35, #FF9A5A);
  color: #fff;
  padding: 12rpx 40rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  border: none;
  box-shadow: 0 8rpx 16rpx rgba(255, 107, 53, 0.2);
}

.calc-btn:active {
  transform: scale(0.96);
}

/* 服务保障样式 */
.guarantee-container {
  background-color: #fff;
  display: flex;
  padding: 20rpx 0;
  justify-content: space-around;
  margin: 0 20rpx;
  border-radius: 16rpx;
}

.guarantee-item {
  display: flex;
  align-items: center;
}

.guarantee-text {
  font-size: 22rpx;
  color: #666;
  margin-left: 8rpx;
}

/* 骨架屏样式 */
.skeleton-container {
  padding-bottom: 40rpx;
}

.search-skeleton {
  height: 80rpx;
  margin: 20rpx 30rpx;
  background-color: #EFEFEF;
  border-radius: 40rpx;
  animation: skeleton-pulse 1.5s infinite;
}

.banner-skeleton {
  height: 340rpx;
  margin: 0 20rpx;
  background-color: #EFEFEF;
  border-radius: 20rpx;
  animation: skeleton-pulse 1.5s infinite;
}

.nav-skeleton {
  display: flex;
  justify-content: space-around;
  padding: 40rpx 20rpx;
  margin: 30rpx 20rpx;
  background-color: #fff;
  border-radius: 20rpx;
}

.nav-item-skeleton {
  width: 100rpx;
  height: 100rpx;
  border-radius: 28rpx;
  background-color: #EFEFEF;
  margin-bottom: 16rpx;
  animation: skeleton-pulse 1.5s infinite;
}

.notice-skeleton {
  height: 80rpx;
  margin: 0 20rpx 30rpx;
  background-color: #EFEFEF;
  border-radius: 16rpx;
  animation: skeleton-pulse 1.5s infinite;
}

.section-skeleton {
  margin: 0 20rpx 30rpx;
  padding: 30rpx;
  background-color: #fff;
  border-radius: 20rpx;
}

.title-skeleton {
  width: 200rpx;
  height: 36rpx;
  margin-bottom: 30rpx;
  background-color: #EFEFEF;
  border-radius: 4rpx;
  animation: skeleton-pulse 1.5s infinite;
}

.courier-skeleton {
  display: flex;
  overflow-x: hidden;
}

.courier-item-skeleton {
  width: 200rpx;
  height: 260rpx;
  margin-right: 24rpx;
  background-color: #EFEFEF;
  border-radius: 16rpx;
  animation: skeleton-pulse 1.5s infinite;
}

.order-skeleton {
  height: 160rpx;
  margin-bottom: 24rpx;
  background-color: #EFEFEF;
  border-radius: 16rpx;
  animation: skeleton-pulse 1.5s infinite;
}

@keyframes skeleton-pulse {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 0.3;
  }
  100% {
    opacity: 0.6;
  }
}

/* 物流追踪列表样式 */
.tracking-list {
  padding: 0;
}

.tracking-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 2rpx solid #f6f6f6;
}

.tracking-item:last-child {
  border-bottom: none;
}

.tracking-company {
  display: flex;
  align-items: center;
}

.tracking-logo {
  width: 60rpx;
  height: 60rpx;
  margin-right: 20rpx;
  border-radius: 8rpx;
}

.tracking-info {
  display: flex;
  flex-direction: column;
}

.tracking-name {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.tracking-number {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
}

.tracking-status {
  display: flex;
  align-items: center;
}

.status-text {
  font-size: 26rpx;
  margin-right: 10rpx;
}

.status-0 {
  color: #999;
}

.status-1, .status-2 {
  color: #3cc51f;
}

.status-3 {
  color: #ff9900;
}

.status-4 {
  color: #ff5500;
}

.status-5 {
  color: #999;
}
</style> 