<template>
  <view class="courier-list-container">
    <!-- 顶部搜索区域 -->
    <view class="search-header">
      <view class="location-picker">
        <uni-icons type="location" size="18" color="#3cc51f"></uni-icons>
        <picker mode="region" @change="regionChange" class="region-picker">
          <text v-if="selectedRegion">{{selectedRegion[0]}} {{selectedRegion[1]}} {{selectedRegion[2]}}</text>
          <text v-else>选择位置</text>
        </picker>
        <uni-icons type="arrowdown" size="14" color="#666"></uni-icons>
      </view>
      <view class="search-box" @click="showSearch">
        <uni-icons type="search" size="16" color="#666"></uni-icons>
        <text class="search-placeholder">搜索快递员</text>
      </view>
    </view>
    
    <!-- 筛选区域 -->
    <view class="filter-bar">
      <view class="filter-item" :class="{ active: currentFilter === 'nearest' }" @click="setFilter('nearest')">
        <text>距离最近</text>
      </view>
      <view class="filter-item" :class="{ active: currentFilter === 'rating' }" @click="setFilter('rating')">
        <text>评分最高</text>
      </view>
      <view class="filter-item" :class="{ active: currentFilter === 'orders' }" @click="setFilter('orders')">
        <text>订单最多</text>
      </view>
    </view>
    
    <!-- 快递员列表 -->
    <scroll-view scroll-y class="courier-scroll" @scrolltolower="loadMore" refresher-enabled @refresherrefresh="refresh" :refresher-triggered="refreshing">
      <!-- 添加加载状态提示 -->
      <view class="loading-state" v-if="courierList.length === 0 && loading">
        <view class="loading-icon"></view>
        <text class="loading-text">正在加载快递员数据...</text>
      </view>
      
      <view class="courier-item" v-for="(item, index) in courierList" :key="index" @click="navigateToDetail(item.id)">
        <image :src="item.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="courier-avatar"></image>
        <view class="courier-info">
          <view class="courier-basic">
            <text class="courier-name">{{ item.name }}</text>
            <view class="rating-box">
              <uni-icons type="star-filled" size="14" color="#ff9900"></uni-icons>
              <text class="rating-text">{{ item.rating }}</text>
            </view>
          </view>
          <view class="courier-stats">
            <text class="stat-item">已完成{{ item.completedOrders }}单</text>
            <text class="stat-item">服务{{ item.serviceTime }}个月</text>
          </view>
          <view class="courier-area">
            <uni-icons type="location" size="14" color="#666"></uni-icons>
            <text class="area-text">{{ item.serviceArea }}</text>
          </view>
          <view class="courier-tags">
            <text class="tag" v-for="(tag, tagIndex) in item.tags" :key="tagIndex">{{ tag }}</text>
          </view>
        </view>
        <view class="distance-info">
          <text class="distance-text">{{ item.distance }}km</text>
          <button class="contact-btn" catchtap="contactCourier" :data-id="item.id">联系</button>
        </view>
      </view>
      
      <!-- 加载更多提示 -->
      <view class="loading-more" v-if="hasMore">
        <text>正在加载更多...</text>
      </view>
      <view class="no-more" v-else>
        <text>没有更多快递员了</text>
      </view>
    </scroll-view>
    
    <!-- 无数据提示 -->
    <view class="empty-tip" v-if="courierList.length === 0 && !loading">
      <image src="/static/images/empty.png" mode="aspectFit" class="empty-image"></image>
      <text class="empty-text">当前区域暂无快递员</text>
      <text class="empty-hint">可以尝试更换区域查找</text>
      <button class="retry-btn" @click="retryLoad">重新加载</button>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
import config from '@/utils/config' // 直接导入配置文件
import api from '@/api' // 导入API模块
import { searchCouriers } from '@/api/search' // 导入搜索快递员API

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      loading: true,
      refreshing: false,
      hasMore: true,
      page: 1,
      selectedRegion: null,
      currentFilter: 'nearest',
      courierList: [],
      latitude: null,
      longitude: null
    };
  },
  
  onLoad() {
    console.log('快递员列表页面加载');
    // 获取当前位置
    this.getCurrentLocation();
    
    // 确保即使位置获取失败，也会加载数据
    setTimeout(() => {
      if (this.courierList.length === 0 && !this.loading) {
        console.log('位置获取可能超时，直接加载数据');
        this.loadCourierData();
      }
    }, 3000);
  },
  
  // 添加onShow生命周期函数，确保每次页面显示时都能加载数据
  onShow() {
    console.log('快递员列表页面显示');
    // 当页面显示时，确保列表已加载
    if (this.courierList.length === 0 && !this.loading) {
      this.loadCourierData();
    }
  },
  
  methods: {
    // 获取当前位置
    getCurrentLocation() {
      console.log('开始获取位置信息');
      // 这里可以调用小程序的定位API
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          console.log('位置获取成功', res);
          // 保存经纬度信息
          this.latitude = res.latitude;
          this.longitude = res.longitude;
          
          // 根据经纬度获取地理位置信息
          uni.request({
            url: 'https://apis.map.qq.com/ws/geocoder/v1/',
            data: {
              location: `${res.latitude},${res.longitude}`,
              key: config.mapKey // 使用导入的config对象
            },
            success: (locationRes) => {
              console.log('地理位置解析成功', locationRes.data);
              if (locationRes.data.status === 0) {
                const result = locationRes.data.result;
                const addressComponent = result.address_component;
                this.selectedRegion = [
                  addressComponent.province,
                  addressComponent.city,
                  addressComponent.district
                ];
                // 加载附近快递员
                this.loadCourierData();
              } else {
                console.warn('地理位置解析返回错误状态', locationRes.data);
                this.loadCourierData();
              }
            },
            fail: (err) => {
              // 即使地址解析失败也应该加载数据
              console.error('地理位置解析失败', err);
              this.loadCourierData();
            }
          });
        },
        fail: (err) => {
          // 位置获取失败，使用赣州的模拟位置
          console.warn('获取位置失败，使用赣州模拟位置', err);
          // 赣州市中心经纬度（大致位置）
          this.latitude = 25.831829;
          this.longitude = 114.935029;
          this.selectedRegion = ['江西省', '赣州市', '章贡区'];
          
          // 在控制台显示模拟位置信息，方便调试
          console.log('模拟位置信息：', {
            latitude: this.latitude,
            longitude: this.longitude,
            region: this.selectedRegion
          });
          
          this.loadCourierData();
        }
      });
    },
    
    // 加载快递员数据
    loadCourierData(append = false) {
      // 修改条件，确保首次加载能够正常进行
      if (this.loading && !this.refreshing && append) return;
      
      console.log('开始加载快递员数据', { page: this.page, append });
      this.loading = true;
      
      // 使用搜索接口获取列表，确保传递空字符串而非undefined
      searchCouriers('', this.page, 10)
        .then(res => {
          console.log('快递员数据加载结果', res);
          if (res.code === 200) {
            const data = res.data;
            
            // 处理列表数据
            let list = data.list || [];
            console.log('获取到快递员列表', list);
            
            // 应用筛选条件
            if (this.selectedRegion && this.selectedRegion[0]) {
              list = list.filter(item => {
                return (!this.selectedRegion[0] || item.province === this.selectedRegion[0]) &&
                       (!this.selectedRegion[1] || item.city === this.selectedRegion[1]) &&
                       (!this.selectedRegion[2] || item.district === this.selectedRegion[2]);
              });
            }
            
            // 排序处理
            if (this.currentFilter === 'rating') {
              list.sort((a, b) => (b.rating || 0) - (a.rating || 0));
            } else if (this.currentFilter === 'orders') {
              list.sort((a, b) => (b.completedOrders || 0) - (a.completedOrders || 0));
            }
            
            // 更新数据
            if (append) {
              this.courierList = [...this.courierList, ...list];
            } else {
              this.courierList = list;
            }
            
            this.hasMore = data.page < data.totalPage;
          } else {
            uni.showToast({
              title: res.message || '获取快递员列表失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('加载快递员数据错误', err);
          uni.showToast({
            title: '网络错误，请稍后重试',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
          if (this.refreshing) {
            this.refreshing = false;
            uni.stopPullDownRefresh();
          }
        });
    },
    
    // 地区选择变化
    regionChange(e) {
      this.selectedRegion = e.detail.value;
      this.page = 1;
      this.courierList = [];
      this.hasMore = true;
      this.loadCourierData();
    },
    
    // 设置筛选条件
    setFilter(filter) {
      if (this.currentFilter === filter) return;
      
      this.currentFilter = filter;
      this.page = 1;
      this.courierList = [];
      this.hasMore = true;
      this.loadCourierData();
    },
    
    // 刷新列表
    refresh() {
      this.refreshing = true;
      this.page = 1;
      this.loadCourierData();
    },
    
    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading) return;
      
      this.page++;
      this.loadCourierData(true);
    },
    
    // 跳转到快递员详情页
    navigateToDetail(id) {
      uni.navigateTo({
        url: `/pages/courier/detail?id=${id}`
      });
    },
    
    // 联系快递员
    contactCourier(e) {
      const id = e.currentTarget.dataset.id;
      const courier = this.courierList.find(item => item.id === id);
      
      if (courier && courier.phone) {
        uni.makePhoneCall({
          phoneNumber: courier.phone
        });
      } else {
        uni.showToast({
          title: '暂无联系方式',
          icon: 'none'
        });
      }
    },
    
    // 显示搜索页面
    showSearch() {
      uni.navigateTo({
        url: '/pages/search/search?type=courier'
      });
    },
    
    // 添加重新加载方法
    retryLoad() {
      this.page = 1;
      uni.showLoading({
        title: '加载中'
      });
      this.loadCourierData();
      setTimeout(() => {
        uni.hideLoading();
      }, 1000);
    }
  }
};
</script>

<style>
.courier-list-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  display: flex;
  flex-direction: column;
}

.search-header {
  background-color: #fff;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.location-picker {
  display: flex;
  align-items: center;
  font-size: 28rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.region-picker {
  margin: 0 8rpx;
  max-width: 180rpx;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.search-box {
  flex: 1;
  height: 60rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
}

.search-placeholder {
  font-size: 26rpx;
  color: #999;
  margin-left: 10rpx;
}

.filter-bar {
  display: flex;
  background-color: #fff;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.filter-item {
  flex: 1;
  text-align: center;
  font-size: 28rpx;
  color: #666;
  position: relative;
}

.filter-item.active {
  color: #3cc51f;
  font-weight: bold;
}

.filter-item.active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: -10rpx;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background-color: #3cc51f;
  border-radius: 2rpx;
}

.courier-scroll {
  flex: 1;
  height: 0;
}

.courier-item {
  display: flex;
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  position: relative;
}

.courier-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-right: 20rpx;
  background-color: #f0f0f0;
  flex-shrink: 0;
}

.courier-info {
  flex: 1;
  overflow: hidden;
}

.courier-basic {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}

.courier-name {
  font-size: 32rpx;
  font-weight: bold;
  margin-right: 16rpx;
}

.rating-box {
  display: flex;
  align-items: center;
}

.rating-text {
  font-size: 24rpx;
  color: #ff9900;
  margin-left: 4rpx;
}

.courier-stats {
  display: flex;
  margin-bottom: 8rpx;
}

.stat-item {
  font-size: 24rpx;
  color: #666;
  margin-right: 16rpx;
}

.courier-area {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}

.area-text {
  font-size: 24rpx;
  color: #666;
  margin-left: 4rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.courier-tags {
  display: flex;
  flex-wrap: wrap;
}

.tag {
  font-size: 22rpx;
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-right: 12rpx;
  margin-bottom: 8rpx;
}

.distance-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  flex-shrink: 0;
}

.distance-text {
  font-size: 24rpx;
  color: #999;
}

.contact-btn {
  min-width: 120rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 26rpx;
  background-color: #3cc51f;
  color: #fff;
  border-radius: 30rpx;
  padding: 0 20rpx;
  margin: 0;
}

.loading-more, .no-more {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 26rpx;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 30rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.empty-hint {
  font-size: 26rpx;
  color: #999;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-icon {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  border: 4rpx solid #3cc51f;
  border-top-color: transparent;
  animation: spin 0.75s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 26rpx;
  color: #666;
}

.retry-btn {
  min-width: 120rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 26rpx;
  background-color: #3cc51f;
  color: #fff;
  border-radius: 30rpx;
  padding: 0 20rpx;
  margin: 0;
}
</style> 