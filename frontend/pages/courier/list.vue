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
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue'

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
      courierList: [
        {
          id: 1,
          name: '张师傅',
          avatar: '/static/images/courier1.jpg',
          rating: 4.9,
          completedOrders: 326,
          serviceTime: 8,
          serviceArea: '江西省 南昌市 青山湖区',
          tags: ['准时送达', '服务好', '有礼貌'],
          distance: 0.8
        },
        {
          id: 2,
          name: '李师傅',
          avatar: '/static/images/courier2.jpg',
          rating: 4.8,
          completedOrders: 215,
          serviceTime: 6,
          serviceArea: '江西省 南昌市 青山湖区',
          tags: ['送货快', '态度好'],
          distance: 1.2
        },
        {
          id: 3,
          name: '王师傅',
          avatar: '/static/images/courier3.jpg',
          rating: 4.7,
          completedOrders: 198,
          serviceTime: 5,
          serviceArea: '江西省 南昌市 青山湖区',
          tags: ['认真负责', '服务周到'],
          distance: 1.5
        },
        {
          id: 4,
          name: '赵师傅',
          avatar: '/static/images/default-avatar.png',
          rating: 4.6,
          completedOrders: 156,
          serviceTime: 4,
          serviceArea: '江西省 南昌市 青山湖区',
          tags: ['准时送达', '沟通顺畅'],
          distance: 2.1
        },
        {
          id: 5,
          name: '钱师傅',
          avatar: '/static/images/default-avatar.png',
          rating: 4.5,
          completedOrders: 132,
          serviceTime: 3,
          serviceArea: '江西省 南昌市 青山湖区',
          tags: ['服务周到', '耐心'],
          distance: 2.8
        }
      ]
    };
  },
  
  onLoad() {
    // 获取当前位置
    this.getCurrentLocation();
    
    // 模拟加载数据
    setTimeout(() => {
      this.loading = false;
    }, 500);
  },
  
  methods: {
    // 获取当前位置
    getCurrentLocation() {
      // 这里可以调用小程序的定位API
      // 示例数据
      this.selectedRegion = ['江西省', '南昌市', '青山湖区'];
    },
    
    // 地区选择变化
    regionChange(e) {
      this.selectedRegion = e.detail.value;
      this.refreshCourierList();
    },
    
    // 设置筛选条件
    setFilter(filter) {
      if (this.currentFilter === filter) return;
      
      this.currentFilter = filter;
      this.page = 1;
      this.refreshCourierList();
    },
    
    // 刷新列表
    refresh() {
      this.refreshing = true;
      this.page = 1;
      
      // 模拟网络请求
      setTimeout(() => {
        this.refreshCourierList();
        this.refreshing = false;
        uni.showToast({
          title: '刷新成功',
          icon: 'success'
        });
      }, 1000);
    },
    
    // 加载更多
    loadMore() {
      if (!this.hasMore || this.loading) return;
      
      this.loading = true;
      this.page++;
      
      // 模拟网络请求
      setTimeout(() => {
        // 示例：当页数超过3时没有更多数据
        if (this.page > 3) {
          this.hasMore = false;
          this.loading = false;
          return;
        }
        
        // 模拟加载更多数据
        const newCouriers = [
          {
            id: 5 + this.page,
            name: '新增师傅' + this.page,
            avatar: '/static/images/default-avatar.png',
            rating: 4.3,
            completedOrders: 100,
            serviceTime: 2,
            serviceArea: '江西省 南昌市 青山湖区',
            tags: ['服务好'],
            distance: 3.0 + this.page
          }
        ];
        
        this.courierList = [...this.courierList, ...newCouriers];
        this.loading = false;
      }, 1000);
    },
    
    // 刷新快递员列表
    refreshCourierList() {
      // 根据筛选条件对列表进行排序
      if (this.currentFilter === 'nearest') {
        this.courierList.sort((a, b) => a.distance - b.distance);
      } else if (this.currentFilter === 'rating') {
        this.courierList.sort((a, b) => b.rating - a.rating);
      } else if (this.currentFilter === 'orders') {
        this.courierList.sort((a, b) => b.completedOrders - a.completedOrders);
      }
      
      // 实际应用中，这里应该调用API获取数据
      this.hasMore = true;
    },
    
    // 显示搜索页面
    showSearch() {
      uni.navigateTo({
        url: '/pages/search/search?type=courier'
      });
    },
    
    // 导航到快递员详情页
    navigateToDetail(id) {
      uni.navigateTo({
        url: `/pages/courier/detail?id=${id}`
      });
    },
    
    // 联系快递员
    contactCourier(e) {
      const id = e.currentTarget.dataset.id;
      const courier = this.courierList.find(item => item.id === id);
      
      // 模拟联系快递员
      uni.showActionSheet({
        itemList: ['拨打电话', '发送消息'],
        success: (res) => {
          if (res.tapIndex === 0) {
            // 拨打电话
            uni.makePhoneCall({
              phoneNumber: '10086', // 这里应该是快递员的电话
              fail: () => {
                uni.showToast({
                  title: '拨打电话失败',
                  icon: 'none'
                });
              }
            });
          } else if (res.tapIndex === 1) {
            // 发送消息
            uni.showToast({
              title: '消息功能开发中',
              icon: 'none'
            });
          }
        }
      });
      
      // 阻止事件冒泡
      return false;
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
</style> 