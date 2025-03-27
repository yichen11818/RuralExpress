<template>
  <view class="station-container">
    <!-- 服务点信息卡片 -->
    <view class="station-card">
      <!-- 服务点基本信息 -->
      <view class="station-header">
        <image :src="stationInfo.logo || '/static/images/station.png'" mode="aspectFit" class="station-logo"></image>
        <view class="station-basic">
          <text class="station-name">{{ stationInfo.name }}</text>
          <view class="operation-hours">
            <uni-icons type="clock" size="14" color="#666"></uni-icons>
            <text class="time-text">{{ stationInfo.businessHours }}</text>
            <text class="status-tag" :class="{ open: stationInfo.isOpen }">{{ stationInfo.isOpen ? '营业中' : '已打烊' }}</text>
          </view>
        </view>
      </view>
      
      <!-- 地址信息 -->
      <view class="location-section" @click="openMap">
        <view class="address-info">
          <text class="address-text">{{ stationInfo.address }}</text>
          <text class="distance-text">{{ stationInfo.distance }}</text>
        </view>
        <uni-icons type="right" size="16" color="#ccc"></uni-icons>
      </view>
      
      <!-- 联系信息 -->
      <view class="contact-section">
        <view class="contact-item" @click="callPhone(stationInfo.phone)">
          <uni-icons type="phone" size="20" color="#3cc51f"></uni-icons>
          <text class="contact-text">{{ stationInfo.phone }}</text>
        </view>
        <view class="contact-divider"></view>
        <view class="contact-item" @click="copyWechat">
          <uni-icons type="weixin" size="20" color="#3cc51f"></uni-icons>
          <text class="contact-text">微信联系</text>
        </view>
      </view>
    </view>
    
    <!-- 服务内容卡片 -->
    <view class="service-card">
      <view class="card-title">服务内容</view>
      <view class="service-grid">
        <view class="service-item" v-for="(item, index) in stationInfo.services" :key="index">
          <view class="service-icon">
            <uni-icons :type="getServiceIcon(item.type)" size="24" color="#3cc51f"></uni-icons>
          </view>
          <text class="service-name">{{ item.name }}</text>
        </view>
      </view>
    </view>
    
    <!-- 快递公司卡片 -->
    <view class="express-card">
      <view class="card-title">支持快递</view>
      <view class="express-list">
        <view class="express-item" v-for="(item, index) in stationInfo.expressCompanies" :key="index">
          <image :src="item.logo || '/static/images/express.png'" mode="aspectFit" class="express-logo"></image>
          <text class="express-name">{{ item.name }}</text>
        </view>
      </view>
    </view>
    
    <!-- 图片展示卡片 -->
    <view class="photos-card" v-if="stationInfo.photos && stationInfo.photos.length > 0">
      <view class="card-title">服务点照片</view>
      <scroll-view class="photos-scroll" scroll-x>
        <view class="photos-list">
          <view 
            class="photo-item" 
            v-for="(item, index) in stationInfo.photos" 
            :key="index"
            @click="previewImage(index)"
          >
            <image :src="item" mode="aspectFill" class="photo-image"></image>
          </view>
        </view>
      </scroll-view>
    </view>
    
    <!-- 用户评价卡片 -->
    <view class="review-card" v-if="stationInfo.reviews && stationInfo.reviews.length > 0">
      <view class="review-header">
        <view class="card-title">用户评价</view>
        <view class="rating-overview">
          <text class="rating-score">{{ stationInfo.rating }}</text>
          <view class="rating-stars">
            <uni-icons 
              v-for="index in 5" 
              :key="index" 
              :type="stationInfo.rating >= index ? 'star-filled' : 'star'" 
              size="14" 
              :color="stationInfo.rating >= index ? '#ff9900' : '#ccc'"
            ></uni-icons>
          </view>
        </view>
      </view>
      
      <view class="review-list">
        <view class="review-item" v-for="(item, index) in stationInfo.reviews" :key="index">
          <view class="reviewer-info">
            <image :src="item.avatar || '/static/images/user.png'" mode="aspectFill" class="reviewer-avatar"></image>
            <view class="reviewer-detail">
              <text class="reviewer-name">{{ item.name }}</text>
              <view class="review-rating">
                <uni-icons 
                  v-for="i in 5" 
                  :key="i" 
                  :type="item.rating >= i ? 'star-filled' : 'star'" 
                  size="12" 
                  :color="item.rating >= i ? '#ff9900' : '#ccc'"
                ></uni-icons>
                <text class="review-time">{{ item.time }}</text>
              </view>
            </view>
          </view>
          <text class="review-content">{{ item.content }}</text>
        </view>
      </view>
      
      <view class="view-more" v-if="stationInfo.reviewCount > 3" @click="viewAllReviews">
        <text>查看全部{{ stationInfo.reviewCount }}条评价</text>
        <uni-icons type="right" size="14" color="#3cc51f"></uni-icons>
      </view>
    </view>
    
    <!-- 周边服务点 -->
    <view class="nearby-card" v-if="nearbyStations.length > 0">
      <view class="card-title">周边服务点</view>
      <view class="nearby-list">
        <view class="nearby-item" v-for="(item, index) in nearbyStations" :key="index" @click="goToStation(item.id)">
          <view class="nearby-info">
            <text class="nearby-name">{{ item.name }}</text>
            <text class="nearby-address">{{ item.address }}</text>
            <text class="nearby-distance">{{ item.distance }}</text>
          </view>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
      </view>
    </view>
    
    <!-- 底部固定按钮 -->
    <view class="bottom-actions">
      <button class="action-btn share-btn" @click="handleShare">
        <uni-icons type="redo" size="18" color="#3cc51f"></uni-icons>
        <text>分享</text>
      </button>
      <button class="action-btn collect-btn" @click="handleCollect">
        <uni-icons :type="isCollected ? 'star-filled' : 'star'" size="18" :color="isCollected ? '#ff9900' : '#3cc51f'"></uni-icons>
        <text>{{ isCollected ? '已收藏' : '收藏' }}</text>
      </button>
      <button class="action-btn navigate-btn" @click="openMap">
        <uni-icons type="location" size="18" color="#fff"></uni-icons>
        <text>导航</text>
      </button>
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
      stationId: null,
      isCollected: false,
      stationInfo: {
        id: 1,
        name: '顺丰快递服务点',
        logo: '/static/images/sf-logo.png',
        address: '南昌市青山湖区艾溪湖北路77号江西师范大学旁',
        distance: '500m',
        businessHours: '08:00-21:00',
        isOpen: true,
        phone: '0791-88123456',
        wechat: 'SF_service123',
        rating: 4.7,
        reviewCount: 56,
        longitude: 115.928852,
        latitude: 28.682724,
        services: [
          { type: 'send', name: '寄件' },
          { type: 'receive', name: '取件' },
          { type: 'pack', name: '打包' },
          { type: 'weight', name: '称重' },
          { type: 'store', name: '寄存' },
          { type: 'locker', name: '智能柜' }
        ],
        expressCompanies: [
          { name: '顺丰速运', logo: '/static/images/sf-logo.png' },
          { name: '中通快递', logo: '/static/images/zt-logo.png' },
          { name: '圆通速递', logo: '/static/images/yt-logo.png' },
          { name: '韵达快递', logo: '/static/images/yd-logo.png' },
          { name: '申通快递', logo: '/static/images/st-logo.png' },
          { name: '京东物流', logo: '/static/images/jd-logo.png' }
        ],
        photos: [
          '/static/images/station-photo-1.jpg',
          '/static/images/station-photo-2.jpg',
          '/static/images/station-photo-3.jpg'
        ],
        reviews: [
          {
            name: '用户1234',
            avatar: '/static/images/user-avatar-1.png',
            rating: 5,
            time: '2023-03-15',
            content: '服务点环境整洁，工作人员态度很好，快递存放整齐，取件速度快，很方便。'
          },
          {
            name: '南昌小王',
            avatar: '/static/images/user-avatar-2.png',
            rating: 4,
            time: '2023-03-10',
            content: '位置很好找，就在小区门口，营业时间长，下班后也能取件，挺不错的。'
          },
          {
            name: '江师大学生',
            avatar: '/static/images/user-avatar-3.png',
            rating: 5,
            time: '2023-03-05',
            content: '学校旁边的快递点，支持多种快递公司，服务态度好，快递来了会及时通知。'
          }
        ]
      },
      nearbyStations: [
        {
          id: 2,
          name: '中通快递营业点',
          address: '南昌市青山湖区解放东路189号',
          distance: '800m'
        },
        {
          id: 3,
          name: '圆通速递服务站',
          address: '南昌市青山湖区高新大道998号',
          distance: '1.2km'
        }
      ]
    };
  },
  
  onLoad(options) {
    if (options.id) {
      this.stationId = options.id;
      this.loadStationInfo();
    } else {
      uni.showToast({
        title: '服务点ID不存在',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  methods: {
    // 加载服务点信息
    loadStationInfo() {
      // 实际应用中，这里应该调用API获取服务点信息
      // 示例：
      /*
      uni.showLoading({
        title: '加载中...'
      });
      
      uni.request({
        url: `https://api.example.com/stations/${this.stationId}`,
        method: 'GET',
        success: (res) => {
          uni.hideLoading();
          if (res.data.success) {
            this.stationInfo = res.data.data;
            this.checkIfCollected();
          } else {
            uni.showToast({
              title: res.data.message || '获取服务点信息失败',
              icon: 'none'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        }
      });
      */
      
      // 模拟加载
      this.checkIfCollected();
    },
    
    // 检查是否已收藏
    checkIfCollected() {
      // 实际应用中，这里应该调用API检查是否已收藏
      // 示例：
      /*
      uni.request({
        url: `https://api.example.com/user/collections/check`,
        method: 'GET',
        data: {
          type: 'station',
          id: this.stationId
        },
        success: (res) => {
          if (res.data.success) {
            this.isCollected = res.data.data.isCollected;
          }
        }
      });
      */
      
      // 模拟检查结果
      this.isCollected = false;
    },
    
    // 获取服务图标
    getServiceIcon(type) {
      const iconMap = {
        send: 'paperplane',
        receive: 'box',
        pack: 'gift',
        weight: 'flag',
        store: 'home',
        locker: 'locked'
      };
      return iconMap[type] || 'help';
    },
    
    // 打开地图导航
    openMap() {
      if (!this.stationInfo.longitude || !this.stationInfo.latitude) {
        return uni.showToast({
          title: '暂无位置信息',
          icon: 'none'
        });
      }
      
      uni.openLocation({
        latitude: this.stationInfo.latitude,
        longitude: this.stationInfo.longitude,
        name: this.stationInfo.name,
        address: this.stationInfo.address,
        fail: () => {
          uni.showToast({
            title: '打开地图失败',
            icon: 'none'
          });
        }
      });
    },
    
    // 拨打电话
    callPhone(phone) {
      if (!phone) return;
      
      uni.makePhoneCall({
        phoneNumber: phone,
        fail: () => {
          uni.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    },
    
    // 复制微信号
    copyWechat() {
      if (!this.stationInfo.wechat) {
        return uni.showToast({
          title: '暂无微信联系方式',
          icon: 'none'
        });
      }
      
      uni.setClipboardData({
        data: this.stationInfo.wechat,
        success: () => {
          uni.showToast({
            title: '微信号已复制',
            icon: 'success'
          });
        }
      });
    },
    
    // 预览图片
    previewImage(index) {
      uni.previewImage({
        urls: this.stationInfo.photos,
        current: this.stationInfo.photos[index]
      });
    },
    
    // 查看全部评价
    viewAllReviews() {
      uni.navigateTo({
        url: `/pages/station/reviews?id=${this.stationId}`
      });
    },
    
    // 跳转到其他服务点
    goToStation(id) {
      uni.navigateTo({
        url: `/pages/station/detail?id=${id}`
      });
    },
    
    // 分享服务点
    handleShare() {
      uni.share({
        provider: 'weixin',
        scene: 'WXSceneSession',
        type: 0,
        title: this.stationInfo.name,
        summary: `地址：${this.stationInfo.address}，营业时间：${this.stationInfo.businessHours}`,
        imageUrl: this.stationInfo.logo || '/static/images/station.png',
        success: (res) => {
          console.log('分享成功', res);
        },
        fail: (err) => {
          console.log('分享失败', err);
        }
      });
    },
    
    // 收藏/取消收藏
    handleCollect() {
      // 实际应用中，这里应该调用API收藏或取消收藏
      // 示例：
      /*
      uni.showLoading({
        title: this.isCollected ? '取消收藏...' : '收藏中...'
      });
      
      uni.request({
        url: `https://api.example.com/user/collections/${this.isCollected ? 'cancel' : 'add'}`,
        method: 'POST',
        data: {
          type: 'station',
          id: this.stationId
        },
        success: (res) => {
          uni.hideLoading();
          if (res.data.success) {
            this.isCollected = !this.isCollected;
            uni.showToast({
              title: this.isCollected ? '收藏成功' : '已取消收藏',
              icon: 'success'
            });
          } else {
            uni.showToast({
              title: res.data.message || '操作失败',
              icon: 'none'
            });
          }
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        }
      });
      */
      
      // 模拟收藏状态切换
      this.isCollected = !this.isCollected;
      uni.showToast({
        title: this.isCollected ? '收藏成功' : '已取消收藏',
        icon: 'success'
      });
    }
  }
};
</script>

<style>
.station-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

/* 通用卡片样式 */
.station-card,
.service-card,
.express-card,
.photos-card,
.review-card,
.nearby-card {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

/* 服务点基本信息样式 */
.station-header {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}

.station-logo {
  width: 100rpx;
  height: 100rpx;
  margin-right: 20rpx;
  border-radius: 10rpx;
}

.station-basic {
  flex: 1;
}

.station-name {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.operation-hours {
  display: flex;
  align-items: center;
}

.time-text {
  font-size: 26rpx;
  color: #666;
  margin-left: 8rpx;
}

.status-tag {
  font-size: 22rpx;
  background-color: #f5f5f5;
  color: #999;
  padding: 4rpx 10rpx;
  border-radius: 4rpx;
  margin-left: 15rpx;
}

.status-tag.open {
  background-color: #e6f7ea;
  color: #3cc51f;
}

/* 地址信息样式 */
.location-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f8f8f8;
  padding: 20rpx;
  border-radius: 8rpx;
  margin-bottom: 30rpx;
}

.address-info {
  flex: 1;
}

.address-text {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 6rpx;
}

.distance-text {
  font-size: 24rpx;
  color: #3cc51f;
}

/* 联系信息样式 */
.contact-section {
  display: flex;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 30rpx;
}

.contact-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.contact-divider {
  width: 1rpx;
  height: 40rpx;
  background-color: #f0f0f0;
}

.contact-text {
  font-size: 28rpx;
  color: #333;
  margin-left: 10rpx;
}

/* 服务内容样式 */
.service-grid {
  display: flex;
  flex-wrap: wrap;
}

.service-item {
  width: 33.33%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30rpx;
}

.service-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  background-color: #f0f8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10rpx;
}

.service-name {
  font-size: 26rpx;
  color: #333;
}

/* 快递公司样式 */
.express-list {
  display: flex;
  flex-wrap: wrap;
}

.express-item {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20rpx;
}

.express-logo {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 10rpx;
}

.express-name {
  font-size: 24rpx;
  color: #333;
}

/* 图片展示样式 */
.photos-scroll {
  width: 100%;
}

.photos-list {
  display: flex;
  white-space: nowrap;
}

.photo-item {
  display: inline-block;
  width: 240rpx;
  height: 180rpx;
  margin-right: 15rpx;
  border-radius: 8rpx;
  overflow: hidden;
}

.photo-item:last-child {
  margin-right: 0;
}

.photo-image {
  width: 100%;
  height: 100%;
}

/* 评价样式 */
.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.rating-overview {
  display: flex;
  align-items: center;
}

.rating-score {
  font-size: 36rpx;
  color: #ff9900;
  font-weight: bold;
  margin-right: 10rpx;
}

.rating-stars {
  display: flex;
}

.review-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.reviewer-info {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
}

.reviewer-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
  margin-right: 15rpx;
}

.reviewer-detail {
  flex: 1;
}

.reviewer-name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 6rpx;
}

.review-rating {
  display: flex;
  align-items: center;
}

.review-time {
  font-size: 22rpx;
  color: #999;
  margin-left: 10rpx;
}

.review-content {
  font-size: 26rpx;
  color: #333;
  line-height: 1.5;
}

.view-more {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
  font-size: 26rpx;
  color: #3cc51f;
}

/* 周边服务点样式 */
.nearby-item {
  display: flex;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.nearby-item:last-child {
  border-bottom: none;
}

.nearby-info {
  flex: 1;
}

.nearby-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 6rpx;
}

.nearby-address {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 6rpx;
}

.nearby-distance {
  font-size: 22rpx;
  color: #3cc51f;
}

/* 底部按钮样式 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  display: flex;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.action-btn {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  margin: 0;
  border-radius: 40rpx;
}

.action-btn text {
  margin-left: 6rpx;
}

.share-btn {
  background-color: #fff;
  color: #3cc51f;
  border: 1rpx solid #3cc51f;
  margin-right: 20rpx;
}

.collect-btn {
  background-color: #fff;
  color: #3cc51f;
  border: 1rpx solid #3cc51f;
  margin-right: 20rpx;
}

.navigate-btn {
  background-color: #3cc51f;
  color: #fff;
}
</style> 