<template>
  <view class="track-container">
    <!-- 物流信息卡片 -->
    <view class="track-card">
      <view class="card-header">
        <view class="company-info">
          <image :src="logistics.logo || '/static/images/icon/package.png'" mode="aspectFit" class="company-logo"></image>
          <view class="company-detail">
            <text class="company-name">{{ logistics.company }}</text>
            <text class="tracking-number">运单号：{{ logistics.trackingNo }}</text>
          </view>
        </view>
        <view class="tracking-status" :class="getStatusClass()">{{ logistics.statusText }}</view>
      </view>
      <view class="courier-info" v-if="logistics.courier">
        <view class="courier-avatar">
          <image :src="logistics.courier.avatar || '/static/images/icon/user.png'" mode="aspectFill" class="avatar-image"></image>
        </view>
        <view class="courier-detail">
          <view class="courier-name">
            <text>{{ logistics.courier.name }}</text>
            <text class="courier-title">配送员</text>
          </view>
          <text class="courier-phone">{{ logistics.courier.phone }}</text>
        </view>
        <button class="contact-btn" @click="callCourier(logistics.courier.phone)">联系配送员</button>
      </view>
      <view class="package-info">
        <view class="info-item">
          <text class="info-label">预计送达</text>
          <text class="info-value">{{ logistics.estimatedTime || '暂无信息' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">收件地址</text>
          <text class="info-value">{{ logistics.address || '暂无信息' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">收件人</text>
          <text class="info-value">{{ logistics.receiver || '暂无信息' }}</text>
        </view>
      </view>
    </view>
    
    <!-- 物流跟踪时间线 -->
    <view class="timeline-container">
      <view class="timeline-header">
        <text class="header-title">物流跟踪</text>
        <text class="refresh-text" @click="refreshTimeline">刷新</text>
      </view>
      <view class="timeline">
        <view class="timeline-item" v-for="(item, index) in logistics.timeline" :key="index" :class="{ active: index === 0 }">
          <view class="timeline-dot" :class="{ active: index === 0 }"></view>
          <view class="timeline-content">
            <view class="timeline-status">{{ item.status }}</view>
            <view class="timeline-time">{{ item.time }}</view>
            <view class="timeline-detail" v-if="item.detail">{{ item.detail }}</view>
          </view>
        </view>
        
        <view class="timeline-no-data" v-if="logistics.timeline.length === 0">
          <text>暂无物流信息</text>
        </view>
      </view>
    </view>
    
    <!-- 相关操作 -->
    <view class="action-container" v-if="logistics.status !== 5">
      <button class="action-btn copy-btn" @click="copyTrackingNo">
        <uni-icons type="paperclip" size="18" color="#666"></uni-icons>
        <text>复制单号</text>
      </button>
      <button class="action-btn share-btn" @click="shareTracking">
        <uni-icons type="redo" size="18" color="#fff"></uni-icons>
        <text>分享</text>
      </button>
    </view>
    
    <!-- 已签收时显示评价按钮 -->
    <view class="review-container" v-if="logistics.status === 5 && !logistics.hasReviewed">
      <button class="review-btn" @click="reviewOrder">
        <uni-icons type="star" size="18" color="#fff"></uni-icons>
        <text>评价订单</text>
      </button>
    </view>
    
    <!-- 通知设置 -->
    <view class="notification-container">
      <view class="notification-title">物流通知</view>
      <view class="notification-options">
        <view class="option-item">
          <text>包裹状态更新通知</text>
          <switch :checked="notifications.statusUpdate" @change="toggleNotification('statusUpdate')" color="#3cc51f"></switch>
        </view>
        <view class="option-item">
          <text>预计到达前通知</text>
          <switch :checked="notifications.estimatedArrival" @change="toggleNotification('estimatedArrival')" color="#3cc51f"></switch>
        </view>
        <view class="option-item">
          <text>已签收通知</text>
          <switch :checked="notifications.signed" @change="toggleNotification('signed')" color="#3cc51f"></switch>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
import { isLoggedIn } from '@/api/auth';
import { getLogisticsInfo } from '@/api/order';

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      trackingNo: '',
      orderId: null,
      loading: false,
      logistics: {
        company: '',
        logo: '/static/images/package.png',
        trackingNo: '',
        status: 0,
        statusText: '等待揽收',
        estimatedTime: '',
        address: '',
        receiver: '',
        hasReviewed: false,
        courier: null,
        timeline: []
      },
      notifications: {
        statusUpdate: true,
        estimatedArrival: true,
        signed: true
      },
      refreshing: false
    };
  },
  
  onLoad(options) {
    if (options.trackingNo) {
      this.trackingNo = options.trackingNo;
      this.loadTrackingInfo();
    } else if (options.orderId) {
      this.orderId = options.orderId;
      this.loadTrackingInfo();
    } else {
      uni.showToast({
        title: '缺少运单号或订单ID',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  onPullDownRefresh() {
    this.refreshTimeline();
  },
  
  // 添加微信小程序分享功能
  onShareAppMessage() {
    return {
      title: `${this.logistics.company}物流轨迹`,
      path: `/pages/order/track?trackingNo=${this.logistics.trackingNo}`,
      imageUrl: '/static/images/icon/package.png',
      desc: `运单号：${this.logistics.trackingNo}，当前状态：${this.logistics.statusText}`
    };
  },
  
  methods: {
    // 加载物流信息
    loadTrackingInfo() {
      if (this.loading) return;
      this.loading = true;
      
      console.log('开始加载物流信息，运单号:', this.trackingNo, '订单ID:', this.orderId);
      
      uni.showLoading({
        title: '加载中...'
      });
      
      // 构建请求参数
      const params = {};
      if (this.trackingNo) {
        params.trackingNo = this.trackingNo;
      } else if (this.orderId) {
        params.orderId = this.orderId;
      }
      
      // 移除模拟数据，直接通过API获取真实数据
      getLogisticsInfo(params)
        .then(res => {
          console.log('物流API响应:', res);
          if (res.code === 200 && res.data) {
            // 更新物流信息
            const logisticsData = res.data;
            console.log('收到的物流数据:', logisticsData);
            
            this.logistics = {
              company: logisticsData.companyName || '未知快递公司',
              logo: logisticsData.companyLogo || '/static/images/icon/package.png',
              trackingNo: logisticsData.trackingNo || this.trackingNo,
              status: logisticsData.status || 0,
              statusText: this.getStatusTextByCode(logisticsData.status),
              estimatedTime: logisticsData.estimatedTime || logisticsData.estimatedDelivery,
              address: logisticsData.address || logisticsData.receiverAddress,
              receiver: logisticsData.receiver || logisticsData.receiverName,
              hasReviewed: logisticsData.hasReviewed || false,
              courier: logisticsData.courier,
              timeline: logisticsData.timeline || logisticsData.traces || []
            };
            
            console.log('设置后的物流数据:', this.logistics);
          } else {
            console.log('API返回非200状态或无数据');
            uni.showToast({
              title: '获取物流信息失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取物流详情失败', err);
          uni.showToast({
            title: '网络错误，请稍后重试',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
          uni.hideLoading();
          uni.stopPullDownRefresh();
          
          // 检查数据完整性，确保页面显示正常
          this.ensureDataIntegrity();
        });
    },
    
    // 确保数据完整性
    ensureDataIntegrity() {
      // 确保必要字段都有值，避免显示"暂无信息"
      if (!this.logistics.company) {
        this.logistics.company = '未知快递公司';
      }
      
      if (!this.logistics.logo) {
        this.logistics.logo = '/static/images/icon/package.png';
      }
      
      if (!this.logistics.statusText) {
        this.logistics.statusText = this.getStatusTextByCode(this.logistics.status);
      }
      
      // 确保时间线存在但不使用模拟数据
      if (!this.logistics.timeline) {
        this.logistics.timeline = [];
      }
      
      console.log('数据完整性检查完成:', this.logistics);
    },
    
    // 获取物流状态文本
    getStatusTextByCode(status) {
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
    
    // 刷新物流时间线
    refreshTimeline() {
      if (this.refreshing) return;
      this.refreshing = true;
      
      uni.showLoading({
        title: '刷新中...'
      });
      
      // 重新加载物流信息
      this.loadTrackingInfo();
      
      setTimeout(() => {
        this.refreshing = false;
        uni.stopPullDownRefresh();
      }, 500);
    },
    
    // 获取物流状态样式类
    getStatusClass() {
      const statusMap = {
        0: 'status-waiting',
        1: 'status-collected',
        2: 'status-transporting',
        3: 'status-arrived',
        4: 'status-delivering',
        5: 'status-signed'
      };
      return statusMap[this.logistics.status] || '';
    },
    
    // 复制运单号
    copyTrackingNo() {
      uni.setClipboardData({
        data: this.logistics.trackingNo,
        success: () => {
          uni.showToast({
            title: '复制成功',
            icon: 'success'
          });
        }
      });
    },
    
    // 分享物流信息
    shareTracking() {
      // 微信小程序不支持uni.share，显示提示并引导用户使用右上角的分享功能
      uni.showModal({
        title: '分享提示',
        content: '请点击右上角的"···"按钮，选择"分享"将物流信息分享给好友',
        showCancel: false,
        success: function (res) {
          if (res.confirm) {
            console.log('用户点击确定');
          }
        }
      });
    },
    
    // 拨打电话联系快递员
    callCourier(phone) {
      if (!phone) return;
      
      // 去除手机号中的星号
      const realPhone = phone.replace(/\*+/g, '');
      
      if (realPhone.length < 11) {
        uni.showToast({
          title: '暂无完整电话号码',
          icon: 'none'
        });
        return;
      }
      
      uni.makePhoneCall({
        phoneNumber: realPhone,
        fail: () => {
          uni.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    },
    
    // 评价订单
    reviewOrder() {
      uni.navigateTo({
        url: `/pages/order/review?id=${this.logistics.orderId}`
      });
    },
    
    // 切换通知设置
    toggleNotification(type) {
      this.notifications[type] = !this.notifications[type];
      
      // 保存通知设置
      this.saveNotificationSettings();
    },
    
    // 保存通知设置
    saveNotificationSettings() {
      // 实际应用中，这里应该调用API保存通知设置
      /*
      uni.request({
        url: 'https://api.example.com/user/notification/settings',
        method: 'POST',
        data: {
          trackingNo: this.trackingNo,
          notifications: this.notifications
        },
        success: (res) => {
          if (res.data.success) {
            // 设置保存成功
          } else {
            uni.showToast({
              title: res.data.message || '设置保存失败',
              icon: 'none'
            });
          }
        },
        fail: () => {
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        }
      });
      */
    }
  }
};
</script>

<style>
.track-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 30rpx;
}

/* 物流信息卡片样式 */
.track-card {
  background-color: #fff;
  margin: 30rpx 30rpx 0;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.company-info {
  display: flex;
  align-items: center;
}

.company-logo {
  width: 80rpx;
  height: 80rpx;
  margin-right: 20rpx;
  border-radius: 10rpx;
}

.company-detail {
  display: flex;
  flex-direction: column;
}

.company-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 6rpx;
}

.tracking-number {
  font-size: 24rpx;
  color: #666;
}

.tracking-status {
  font-size: 28rpx;
  font-weight: bold;
}

.status-waiting {
  color: #999;
}

.status-collected, .status-transporting {
  color: #3cc51f;
}

.status-arrived {
  color: #ff9900;
}

.status-delivering {
  color: #ff5500;
}

.status-signed {
  color: #999;
}

/* 快递员信息样式 */
.courier-info {
  display: flex;
  align-items: center;
  background-color: #f8f8f8;
  padding: 20rpx;
  border-radius: 8rpx;
  margin-bottom: 30rpx;
}

.courier-avatar {
  margin-right: 20rpx;
}

.avatar-image {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
}

.courier-detail {
  flex: 1;
}

.courier-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 6rpx;
  display: flex;
  align-items: center;
}

.courier-title {
  font-size: 22rpx;
  color: #3cc51f;
  border: 1rpx solid #3cc51f;
  padding: 2rpx 8rpx;
  border-radius: 6rpx;
  margin-left: 12rpx;
  font-weight: normal;
}

.courier-phone {
  font-size: 24rpx;
  color: #666;
}

.contact-btn {
  background-color: #3cc51f;
  color: #fff;
  font-size: 24rpx;
  padding: 10rpx 20rpx;
  margin: 0;
  height: auto;
  line-height: 1.5;
  border-radius: 30rpx;
}

/* 包裹信息样式 */
.package-info {
  display: flex;
  flex-direction: column;
}

.info-item {
  display: flex;
  margin-bottom: 16rpx;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  width: 150rpx;
  font-size: 26rpx;
  color: #999;
}

.info-value {
  flex: 1;
  font-size: 26rpx;
  color: #333;
}

/* 物流时间线样式 */
.timeline-container {
  background-color: #fff;
  margin: 30rpx 30rpx 0;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.header-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.refresh-text {
  font-size: 26rpx;
  color: #3cc51f;
}

.timeline {
  position: relative;
  padding-left: 30rpx;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 8rpx;
  top: 10rpx;
  bottom: 0;
  width: 2rpx;
  background-color: #e0e0e0;
}

.timeline-item {
  position: relative;
  padding-bottom: 30rpx;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: -30rpx;
  top: 10rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background-color: #e0e0e0;
  z-index: 1;
}

.timeline-dot.active {
  background-color: #3cc51f;
  border: 4rpx solid rgba(60, 197, 31, 0.2);
  width: 20rpx;
  height: 20rpx;
  left: -32rpx;
  top: 8rpx;
}

.timeline-content {
  margin-left: 10rpx;
}

.timeline-status {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 4rpx;
}

.timeline-item.active .timeline-status {
  color: #3cc51f;
  font-weight: bold;
}

.timeline-time {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.timeline-detail {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
}

.timeline-no-data {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 28rpx;
}

/* 操作按钮样式 */
.action-container {
  display: flex;
  margin: 30rpx;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  margin: 0;
}

.action-btn text {
  margin-left: 10rpx;
}

.copy-btn {
  background-color: #f0f0f0;
  color: #666;
  margin-right: 20rpx;
}

.share-btn {
  background-color: #3cc51f;
  color: #fff;
}

/* 评价按钮样式 */
.review-container {
  margin: 30rpx;
}

.review-btn {
  width: 100%;
  height: 80rpx;
  background-color: #ff9900;
  color: #fff;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  margin: 0;
}

.review-btn text {
  margin-left: 10rpx;
}

/* 通知设置样式 */
.notification-container {
  background-color: #fff;
  margin: 30rpx;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.notification-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.option-item:last-child {
  border-bottom: none;
}

.option-item text {
  font-size: 28rpx;
  color: #333;
}
</style> 