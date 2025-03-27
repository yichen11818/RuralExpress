<template>
  <view class="review-detail-container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <view class="back-icon" @click="goBack">
        <uni-icons type="left" size="20" color="#000"></uni-icons>
      </view>
      <text class="header-title">评价详情</text>
    </view>
    
    <!-- 订单信息 -->
    <view class="order-info-section">
      <view class="order-title">订单信息</view>
      <view class="order-detail">
        <view class="order-item">
          <text class="item-label">快递公司</text>
          <text class="item-value">{{ orderInfo.company }}</text>
        </view>
        <view class="order-item">
          <text class="item-label">运单号</text>
          <text class="item-value">{{ orderInfo.trackingNo }}</text>
        </view>
        <view class="order-item">
          <text class="item-label">配送员</text>
          <text class="item-value">{{ orderInfo.courierName }}</text>
        </view>
        <view class="order-item">
          <text class="item-label">订单时间</text>
          <text class="item-value">{{ orderInfo.createTime }}</text>
        </view>
      </view>
    </view>
    
    <!-- 评分区域 -->
    <view class="rating-section">
      <view class="section-title">服务评分</view>
      <view class="rating-wrapper">
        <text class="rating-text">{{ ratingTexts[reviewData.rating - 1] }}</text>
        <view class="star-container">
          <uni-icons
            v-for="index in 5"
            :key="index"
            type="star-filled"
            size="36"
            :color="reviewData.rating >= index ? '#ff9900' : '#ccc'"
          ></uni-icons>
        </view>
      </view>
    </view>
    
    <!-- 快递服务评价 -->
    <view class="service-section" v-if="reviewData.serviceTags && reviewData.serviceTags.length > 0">
      <view class="section-title">配送服务评价</view>
      <view class="tag-container">
        <view
          v-for="(tag, index) in reviewData.serviceTags"
          :key="index"
          class="tag-item"
        >
          {{ tag }}
        </view>
      </view>
    </view>
    
    <!-- 快递员评价 -->
    <view class="courier-section" v-if="reviewData.courierTags && reviewData.courierTags.length > 0">
      <view class="section-title">快递员服务评价</view>
      <view class="tag-container">
        <view
          v-for="(tag, index) in reviewData.courierTags"
          :key="index"
          class="tag-item"
        >
          {{ tag }}
        </view>
      </view>
    </view>
    
    <!-- 评价内容 -->
    <view class="comment-section" v-if="reviewData.comment">
      <view class="section-title">评价内容</view>
      <view class="comment-wrapper">
        <text class="comment-text">{{ reviewData.comment }}</text>
      </view>
    </view>
    
    <!-- 评价图片 -->
    <view class="images-section" v-if="reviewData.images && reviewData.images.length > 0">
      <view class="section-title">评价图片</view>
      <view class="images-container">
        <view class="image-item" v-for="(item, index) in reviewData.images" :key="index">
          <image :src="item" mode="aspectFill" class="review-image" @click="previewImage(index)"></image>
        </view>
      </view>
    </view>
    
    <!-- 评价时间 -->
    <view class="time-section">
      <text class="review-time">评价时间：{{ formatDate(reviewData.createTime) }}</text>
      <text class="review-anonymous" v-if="reviewData.isAnonymous">（匿名评价）</text>
    </view>
    
    <!-- 评价有用 -->
    <view class="useful-section" v-if="!isOwnReview">
      <text class="useful-title">这条评价对您有帮助吗？</text>
      <view class="useful-buttons">
        <button class="useful-btn" :class="{ active: isUseful === true }" @click="markUseful(true)">
          <uni-icons type="thumb-up" size="18" :color="isUseful === true ? '#3cc51f' : '#666'"></uni-icons>
          <text>有帮助</text>
        </button>
        <button class="useful-btn" :class="{ active: isUseful === false }" @click="markUseful(false)">
          <uni-icons type="thumb-down" size="18" :color="isUseful === false ? '#ff5a5f' : '#666'"></uni-icons>
          <text>没帮助</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue';
import { getOrderDetail, getOrderReview } from '@/api/order';
import { isLoggedIn, getUserInfo } from '@/api/auth';

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      orderId: null,
      orderInfo: {
        id: '',
        company: '',
        trackingNo: '',
        courierName: '',
        createTime: ''
      },
      reviewData: {
        id: '',
        orderId: '',
        userId: '',
        rating: 5,
        serviceTags: [],
        courierTags: [],
        comment: '',
        images: [],
        isAnonymous: false,
        createTime: ''
      },
      ratingTexts: ['很差', '较差', '一般', '较好', '很好'],
      isUseful: null,
      userId: '',
      isOwnReview: false
    };
  },
  
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      this.loadData();
    } else {
      uni.showToast({
        title: '订单ID不存在',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
    
    // 获取当前用户ID
    if (isLoggedIn()) {
      getUserInfo().then(res => {
        if (res.success) {
          this.userId = res.data.id;
          this.checkIsOwnReview();
        }
      });
    }
  },
  
  methods: {
    // 返回
    goBack() {
      uni.navigateBack();
    },
    
    // 加载数据
    loadData() {
      uni.showLoading({
        title: '加载中...'
      });
      
      Promise.all([
        getOrderDetail(this.orderId),
        getOrderReview(this.orderId)
      ])
        .then(([orderRes, reviewRes]) => {
          uni.hideLoading();
          
          if (orderRes.success) {
            this.orderInfo = orderRes.data;
          }
          
          if (reviewRes.success) {
            this.reviewData = reviewRes.data;
            this.checkIsOwnReview();
          } else {
            uni.showToast({
              title: '该订单暂无评价',
              icon: 'none'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        })
        .catch(() => {
          uni.hideLoading();
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        });
    },
    
    // 检查是否是自己的评价
    checkIsOwnReview() {
      if (this.userId && this.reviewData.userId) {
        this.isOwnReview = this.userId === this.reviewData.userId;
      }
    },
    
    // 预览图片
    previewImage(index) {
      uni.previewImage({
        urls: this.reviewData.images,
        current: this.reviewData.images[index]
      });
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '';
      
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },
    
    // 标记评价是否有用
    markUseful(useful) {
      // 已经选择过，不能再次选择
      if (this.isUseful !== null) {
        return;
      }
      
      this.isUseful = useful;
      
      // 发送请求到服务器（示例）
      /*
      uni.request({
        url: `https://api.example.com/reviews/${this.reviewData.id}/useful`,
        method: 'POST',
        data: {
          useful: useful
        },
        success: (res) => {
          if (res.data.success) {
            uni.showToast({
              title: '感谢您的反馈',
              icon: 'success'
            });
          }
        }
      });
      */
      
      // 提示用户
      uni.showToast({
        title: '感谢您的反馈',
        icon: 'success'
      });
    }
  }
};
</script>

<style>
.review-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.header {
  position: relative;
  height: 90rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1rpx solid #eee;
}

.back-icon {
  position: absolute;
  left: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-title {
  font-size: 34rpx;
  font-weight: bold;
}

/* 通用section样式 */
.order-info-section,
.rating-section,
.service-section,
.courier-section,
.comment-section,
.images-section,
.time-section,
.useful-section {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.section-title,
.order-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

/* 订单信息样式 */
.order-detail {
  background-color: #f8f8f8;
  border-radius: 8rpx;
  padding: 20rpx;
}

.order-item {
  display: flex;
  margin-bottom: 16rpx;
}

.order-item:last-child {
  margin-bottom: 0;
}

.item-label {
  width: 150rpx;
  font-size: 28rpx;
  color: #666;
}

.item-value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

/* 评分区域样式 */
.rating-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.rating-text {
  font-size: 32rpx;
  color: #ff9900;
  margin-bottom: 20rpx;
}

.star-container {
  display: flex;
}

/* 标签样式 */
.tag-container {
  display: flex;
  flex-wrap: wrap;
}

.tag-item {
  background-color: #f5f5f5;
  border-radius: 30rpx;
  padding: 12rpx 25rpx;
  font-size: 26rpx;
  color: #666;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
}

/* 评价内容样式 */
.comment-wrapper {
  background-color: #f5f5f5;
  border-radius: 8rpx;
  padding: 20rpx;
}

.comment-text {
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
}

/* 评价图片样式 */
.images-container {
  display: flex;
  flex-wrap: wrap;
}

.image-item {
  width: 30%;
  height: 180rpx;
  margin-right: 3%;
  margin-bottom: 20rpx;
  border-radius: 8rpx;
  overflow: hidden;
}

.image-item:nth-child(3n) {
  margin-right: 0;
}

.review-image {
  width: 100%;
  height: 100%;
}

/* 评价时间样式 */
.time-section {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.review-time {
  font-size: 24rpx;
  color: #999;
}

.review-anonymous {
  font-size: 24rpx;
  color: #999;
  margin-left: 10rpx;
}

/* 评价有用样式 */
.useful-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.useful-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
}

.useful-buttons {
  display: flex;
}

.useful-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  padding: 10rpx 25rpx;
  margin: 0 20rpx;
  border: none;
  font-size: 26rpx;
  color: #666;
}

.useful-btn.active {
  background-color: #e6f7ea;
  color: #3cc51f;
}

.useful-btn text {
  margin-left: 10rpx;
}

.useful-btn.active:last-child {
  background-color: #ffedee;
  color: #ff5a5f;
}
</style> 