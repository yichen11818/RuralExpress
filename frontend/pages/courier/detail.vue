<template>
  <view class="detail-container">
    <!-- 头部信息 -->
    <view class="header-section">
      <view class="courier-basic">
        <image :src="courierInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="courier-avatar"></image>
        <view class="courier-info">
          <view class="courier-name-row">
            <text class="courier-name">{{ courierInfo.name }}</text>
            <view class="courier-tag" v-if="courierInfo.serviceStatus === 1">接单中</view>
            <view class="courier-tag off" v-else>休息中</view>
          </view>
          <view class="courier-rating">
            <uni-icons type="star-filled" size="14" color="#ff9900"></uni-icons>
            <text class="rating-text">{{ courierInfo.rating }}</text>
            <text class="rating-count">({{ courierInfo.ratingCount }}条评价)</text>
          </view>
          <view class="courier-stats">
            <view class="stat-item">
              <text class="stat-value">{{ courierInfo.completedOrders }}</text>
              <text class="stat-label">已完成</text>
            </view>
            <view class="stat-divider"></view>
            <view class="stat-item">
              <text class="stat-value">{{ courierInfo.serviceTime }}个月</text>
              <text class="stat-label">服务时长</text>
            </view>
            <view class="stat-divider"></view>
            <view class="stat-item">
              <text class="stat-value">{{ courierInfo.responseTime }}分钟</text>
              <text class="stat-label">平均响应</text>
            </view>
          </view>
        </view>
      </view>
      
      <view class="action-buttons">
        <button class="action-btn call-btn" @click="callCourier">
          <uni-icons type="phone-filled" size="20" color="#fff"></uni-icons>
          <text>联系快递员</text>
        </button>
        <button class="action-btn order-btn" @click="createOrder">
          <uni-icons type="plusempty" size="20" color="#fff"></uni-icons>
          <text>寄件下单</text>
        </button>
      </view>
    </view>
    
    <!-- 服务信息 -->
    <view class="info-section">
      <view class="section-title">服务信息</view>
      <view class="info-item">
        <text class="info-label">服务区域</text>
        <text class="info-value">{{ courierInfo.serviceArea }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">服务时间</text>
        <text class="info-value">{{ courierInfo.serviceTime ? '每天 ' + courierInfo.workStartTime + ' - ' + courierInfo.workEndTime : '暂无' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">交通工具</text>
        <text class="info-value">{{ courierInfo.vehicle }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">简介</text>
        <text class="info-value">{{ courierInfo.introduction || '这位快递员很懒，还没有填写简介~' }}</text>
      </view>
    </view>
    
    <!-- 服务标签 -->
    <view class="tags-section">
      <view class="section-title">服务标签</view>
      <view class="tags-list">
        <text class="tag" v-for="(tag, index) in courierInfo.tags" :key="index">{{ tag }}</text>
      </view>
    </view>
    
    <!-- 评价列表 -->
    <view class="review-section">
      <view class="section-header">
        <view class="section-title">用户评价</view>
        <view class="review-filter" @click="showFilterOptions">
          <text>{{ reviewFilter }}</text>
          <uni-icons type="arrowdown" size="14" color="#666"></uni-icons>
        </view>
      </view>
      
      <view class="review-list" v-if="reviews.length > 0">
        <view class="review-item" v-for="(item, index) in reviews" :key="index">
          <view class="review-user">
            <image :src="item.userAvatar" mode="aspectFill" class="user-avatar"></image>
            <view class="user-info">
              <text class="user-name">{{ item.userName }}</text>
              <text class="review-time">{{ item.time }}</text>
            </view>
            <view class="review-rating">
              <uni-icons v-for="i in 5" :key="i" :type="i <= item.rating ? 'star-filled' : 'star'" size="14" :color="i <= item.rating ? '#ff9900' : '#ddd'"></uni-icons>
            </view>
          </view>
          <text class="review-content">{{ item.content }}</text>
          <view class="order-info" v-if="item.orderInfo">
            <text class="order-label">订单信息：</text>
            <text class="order-value">{{ item.orderInfo }}</text>
          </view>
          <view class="courier-reply" v-if="item.reply">
            <text class="reply-label">快递员回复：</text>
            <text class="reply-content">{{ item.reply }}</text>
          </view>
        </view>
      </view>
      
      <view class="empty-review" v-else>
        <image src="/static/images/empty.png" mode="aspectFit" class="empty-image"></image>
        <text>暂无评价</text>
      </view>
      
      <view class="load-more" v-if="reviews.length > 0 && hasMoreReviews" @click="loadMoreReviews">
        <text>加载更多评价</text>
      </view>
    </view>
  </view>
</template>

<script>
import uniIcons from '../../uni_modules/uni-icons/components/uni-icons/uni-icons.vue'

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      courierId: null,
      reviewFilter: '全部评价',
      reviewPage: 1,
      hasMoreReviews: true,
      courierInfo: {
        id: 1,
        name: '张师傅',
        avatar: '/static/images/courier1.jpg',
        rating: 4.9,
        ratingCount: 326,
        completedOrders: 526,
        serviceTime: 8,
        responseTime: 15,
        serviceStatus: 1, // 0-休息中，1-接单中
        serviceArea: '江西省南昌市青山湖区湖坊镇、艾溪湖区域',
        workStartTime: '08:00',
        workEndTime: '20:00',
        vehicle: '电动三轮车',
        introduction: '您好，我是张师傅，本地人，熟悉社区环境。送件准时，服务热情，欢迎下单！',
        tags: ['准时送达', '服务态度好', '有礼貌', '送货快', '认真负责']
      },
      reviews: [
        {
          id: 1,
          userName: '客户152****8899',
          userAvatar: '/static/images/default-avatar.png',
          rating: 5,
          content: '送件非常及时，态度很好，下雨天还能及时送达，给五星好评！',
          time: '2023-03-18',
          orderInfo: '文件快递 3公里 2023-03-18 送达',
          reply: '感谢您的认可，我们会继续努力提供更好的服务！'
        },
        {
          id: 2,
          userName: '客户138****6677',
          userAvatar: '/static/images/default-avatar.png',
          rating: 5,
          content: '快递员很专业，打包很仔细，而且提前跟我确认了送达时间，非常满意。',
          time: '2023-03-15',
          orderInfo: '小件快递 5公里 2023-03-15 送达'
        },
        {
          id: 3,
          userName: '客户186****3344',
          userAvatar: '/static/images/default-avatar.png',
          rating: 4,
          content: '送货速度挺快的，但是没有提前打电话通知我，其他都挺好的。',
          time: '2023-03-10',
          orderInfo: '中件快递 2公里 2023-03-10 送达',
          reply: '非常抱歉给您带来不便，我们会改进服务流程，下次一定提前联系您！'
        }
      ]
    };
  },
  
  onLoad(options) {
    if (options.id) {
      this.courierId = options.id;
      // 在实际应用中，这里应该根据ID请求快递员信息
      // this.getCourierInfo(this.courierId);
    }
  },
  
  methods: {
    // 请求快递员信息
    getCourierInfo(id) {
      // 这里应该请求后端API获取快递员信息
      // 示例：
      /*
      uni.request({
        url: `https://api.example.com/courier/${id}`,
        method: 'GET',
        success: (res) => {
          if (res.data.success) {
            this.courierInfo = res.data.data;
          } else {
            uni.showToast({
              title: res.data.message || '获取快递员信息失败',
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
    },
    
    // 请求评价列表
    getReviews() {
      // 这里应该请求后端API获取评价列表
      // 示例：
      /*
      uni.request({
        url: `https://api.example.com/courier/${this.courierId}/reviews`,
        method: 'GET',
        data: {
          page: this.reviewPage,
          filter: this.reviewFilter === '全部评价' ? '' : this.reviewFilter
        },
        success: (res) => {
          if (res.data.success) {
            if (this.reviewPage === 1) {
              this.reviews = res.data.data;
            } else {
              this.reviews = [...this.reviews, ...res.data.data];
            }
            this.hasMoreReviews = res.data.hasMore;
          } else {
            uni.showToast({
              title: res.data.message || '获取评价失败',
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
    },
    
    // 加载更多评价
    loadMoreReviews() {
      this.reviewPage++;
      this.getReviews();
    },
    
    // 显示筛选选项
    showFilterOptions() {
      uni.showActionSheet({
        itemList: ['全部评价', '好评', '中评', '差评'],
        success: (res) => {
          const filters = ['全部评价', '好评', '中评', '差评'];
          this.reviewFilter = filters[res.tapIndex];
          this.reviewPage = 1;
          this.getReviews();
        }
      });
    },
    
    // 联系快递员
    callCourier() {
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
    },
    
    // 创建订单
    createOrder() {
      uni.navigateTo({
        url: `/pages/delivery/send?courierId=${this.courierId}`
      });
    }
  }
};
</script>

<style>
.detail-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

/* 头部信息样式 */
.header-section {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.courier-basic {
  display: flex;
}

.courier-avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 80rpx;
  background-color: #f0f0f0;
  margin-right: 30rpx;
  flex-shrink: 0;
}

.courier-info {
  flex: 1;
}

.courier-name-row {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.courier-name {
  font-size: 36rpx;
  font-weight: bold;
  margin-right: 20rpx;
}

.courier-tag {
  font-size: 22rpx;
  color: #fff;
  background-color: #3cc51f;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
}

.courier-tag.off {
  background-color: #999;
}

.courier-rating {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.rating-text {
  font-size: 24rpx;
  color: #ff9900;
  margin: 0 8rpx;
}

.rating-count {
  font-size: 24rpx;
  color: #999;
}

.courier-stats {
  display: flex;
  align-items: center;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 4rpx;
}

.stat-label {
  font-size: 22rpx;
  color: #999;
}

.stat-divider {
  width: 1rpx;
  height: 50rpx;
  background-color: #eee;
  margin: 0 30rpx;
}

.action-buttons {
  display: flex;
  margin-top: 30rpx;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #fff;
  margin: 0;
}

.action-btn text {
  margin-left: 8rpx;
}

.call-btn {
  background-color: #ff9900;
  margin-right: 20rpx;
}

.order-btn {
  background-color: #3cc51f;
}

/* 服务信息样式 */
.info-section, .tags-section, .review-section {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.info-item {
  display: flex;
  margin-bottom: 16rpx;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 28rpx;
  color: #666;
  width: 160rpx;
  flex-shrink: 0;
}

.info-value {
  font-size: 28rpx;
  color: #333;
  flex: 1;
}

/* 标签样式 */
.tags-list {
  display: flex;
  flex-wrap: wrap;
}

.tag {
  font-size: 24rpx;
  color: #3cc51f;
  background-color: rgba(60, 197, 31, 0.1);
  padding: 8rpx 20rpx;
  border-radius: 24rpx;
  margin-right: 20rpx;
  margin-bottom: 16rpx;
}

/* 评价样式 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.review-filter {
  display: flex;
  align-items: center;
  font-size: 26rpx;
  color: #666;
}

.review-filter text {
  margin-right: 6rpx;
}

.review-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-user {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.user-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 32rpx;
  margin-right: 16rpx;
  background-color: #f0f0f0;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 4rpx;
}

.review-time {
  font-size: 24rpx;
  color: #999;
}

.review-rating {
  display: flex;
}

.review-content {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  line-height: 1.5;
}

.order-info {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 16rpx;
}

.courier-reply {
  background-color: #f8f8f8;
  padding: 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #666;
}

.reply-label {
  color: #3cc51f;
  margin-right: 8rpx;
}

.empty-review {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 28rpx;
}

.empty-image {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 20rpx;
}

.load-more {
  text-align: center;
  padding: 20rpx 0;
  color: #3cc51f;
  font-size: 28rpx;
  margin-top: 20rpx;
}
</style> 