<template>
  <view class="evaluate-container">
    <!-- 顶部导航 -->
    <view class="nav-header">
      <view class="back-icon" @click="goBack">
        <uni-icons type="arrow-left" size="24" color="#333"></uni-icons>
      </view>
      <view class="page-title">订单评价</view>
    </view>
    
    <!-- 订单信息卡片 -->
    <view class="order-card">
      <view class="order-header">
        <text class="order-id">订单号：{{ orderInfo.orderNo || '暂无' }}</text>
        <text class="order-status status-5">已送达</text>
      </view>
      <view class="order-content">
        <view class="address-info">
          <view class="address-item">
            <view class="address-icon sender">寄</view>
            <view class="address-detail">
              <view class="name-phone">
                <text class="name">{{ orderInfo.senderName || '暂无' }}</text>
                <text class="phone">{{ formatPhone(orderInfo.senderPhone) }}</text>
              </view>
              <text class="address">{{ orderInfo.senderAddress || '暂无' }}</text>
            </view>
          </view>
          <view class="address-item">
            <view class="address-icon receiver">收</view>
            <view class="address-detail">
              <view class="name-phone">
                <text class="name">{{ orderInfo.receiverName || '暂无' }}</text>
                <text class="phone">{{ formatPhone(orderInfo.receiverPhone) }}</text>
              </view>
              <text class="address">{{ orderInfo.receiverAddress || '暂无' }}</text>
            </view>
          </view>
        </view>
        <view class="courier-info" v-if="orderInfo.courierName">
          <image class="courier-avatar" :src="orderInfo.courierAvatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
          <view class="courier-detail">
            <text class="courier-name">{{ orderInfo.courierName }}</text>
            <text class="courier-desc">{{ orderInfo.courierCompany || '乡递快递' }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 评分区域 -->
    <view class="section rating-section">
      <view class="section-title">配送满意度</view>
      <view class="star-rating">
        <view class="star-text">{{ ratingLevel }}</view>
        <view class="stars">
          <uni-icons 
            v-for="i in 5" 
            :key="i" 
            :type="rating >= i ? 'star-filled' : 'star'" 
            size="32" 
            :color="rating >= i ? '#ff9900' : '#dddddd'"
            @click="setRating(i)"
          ></uni-icons>
        </view>
      </view>
    </view>
    
    <!-- 评价标签 -->
    <view class="section tags-section">
      <view class="section-title">评价标签（可多选）</view>
      <view class="tags-container">
        <view 
          v-for="(tag, index) in evaluateTags" 
          :key="index"
          class="tag-item"
          :class="{ active: selectedTags.includes(tag) }"
          @click="toggleTag(tag)"
        >
          {{ tag }}
        </view>
      </view>
    </view>
    
    <!-- 评价内容 -->
    <view class="section content-section">
      <view class="section-title">评价内容</view>
      <textarea 
        class="content-textarea" 
        placeholder="请输入您对本次配送的评价，您的反馈将帮助我们提供更好的服务..." 
        v-model="content"
        maxlength="200"
      ></textarea>
      <view class="content-counter">{{ content.length }}/200</view>
    </view>
    
    <!-- 上传图片 -->
    <view class="section upload-section">
      <view class="section-title">上传图片（选填）</view>
      <view class="upload-container">
        <view 
          class="image-item" 
          v-for="(image, index) in images" 
          :key="index"
        >
          <image class="preview-image" :src="image" mode="aspectFill" @click="previewImage(index)"></image>
          <view class="delete-btn" @click.stop="deleteImage(index)">
            <uni-icons type="close" size="16" color="#ffffff"></uni-icons>
          </view>
        </view>
        <view class="upload-btn" @click="chooseImage" v-if="images.length < 6">
          <uni-icons type="camera" size="28" color="#999999"></uni-icons>
          <text class="upload-text">添加图片</text>
        </view>
      </view>
      <view class="upload-tip">最多上传6张图片</view>
    </view>
    
    <!-- 匿名评价 -->
    <view class="section anonymous-section">
      <view class="anonymous-row">
        <text>匿名评价</text>
        <switch :checked="isAnonymous" @change="toggleAnonymous" color="#3cc51f" />
      </view>
      <text class="anonymous-tip">匿名评价将不会显示您的个人信息</text>
    </view>
    
    <!-- 提交按钮 -->
    <view class="submit-section">
      <button class="submit-btn" :disabled="!isValid" @click="submitEvaluate">提交评价</button>
    </view>
  </view>
</template>

<script>
import { getOrderDetail, submitOrderReview, uploadReviewImage } from '@/api/order';

export default {
  data() {
    return {
      orderId: null,
      orderInfo: {},
      rating: 5,
      selectedTags: [],
      content: '',
      images: [],
      isAnonymous: false,
      loading: false,
      evaluateTags: [
        '服务态度好',
        '快递准时',
        '穿戴整洁',
        '派件速度快',
        '不耽误工作',
        '详细告知取件地点',
        '耐心讲解',
        '包装完好'
      ]
    };
  },
  computed: {
    ratingLevel() {
      const levels = ['非常差', '差', '一般', '好', '非常好'];
      return levels[this.rating - 1] || '请评分';
    },
    isValid() {
      // 评分必填，其他选填
      return this.rating > 0 && !this.loading;
    }
  },
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      this.loadOrderDetail();
    } else {
      uni.showToast({
        title: '无效的订单ID',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  methods: {
    // 加载订单详情
    loadOrderDetail() {
      uni.showLoading({
        title: '加载中...'
      });
      
      getOrderDetail(this.orderId)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.orderInfo = res.data;
            
            // 检查订单状态是否允许评价
            if (res.data.status !== 5 && res.data.status !== 6) {
              uni.showToast({
                title: '当前订单状态不能评价',
                icon: 'none'
              });
              setTimeout(() => {
                uni.navigateBack();
              }, 1500);
            }
          } else {
            uni.showToast({
              title: res.message || '获取订单详情失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取订单详情失败', err);
          uni.showToast({
            title: '网络请求失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    // 设置评分
    setRating(score) {
      this.rating = score;
    },
    // 切换标签
    toggleTag(tag) {
      const index = this.selectedTags.indexOf(tag);
      if (index > -1) {
        this.selectedTags.splice(index, 1);
      } else {
        this.selectedTags.push(tag);
      }
    },
    // 选择图片
    chooseImage() {
      uni.chooseImage({
        count: 6 - this.images.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.images = [...this.images, ...res.tempFilePaths];
        }
      });
    },
    // 预览图片
    previewImage(index) {
      uni.previewImage({
        urls: this.images,
        current: this.images[index]
      });
    },
    // 删除图片
    deleteImage(index) {
      this.images.splice(index, 1);
    },
    // 切换匿名状态
    toggleAnonymous(e) {
      this.isAnonymous = e.detail.value;
    },
    // 格式化手机号
    formatPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '****' + phone.substring(7);
    },
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    // 提交评价
    submitEvaluate() {
      if (!this.isValid || this.loading) return;
      
      this.loading = true;
      uni.showLoading({
        title: '提交中...'
      });
      
      // 先上传图片，再提交评价
      const uploadImages = () => {
        if (this.images.length === 0) {
          return Promise.resolve([]);
        }
        
        const promises = this.images.map(image => {
          return new Promise((resolve, reject) => {
            const uploadTask = uni.uploadFile({
              url: 'http://localhost:8080/api/order/review/upload',
              filePath: image,
              name: 'file',
              success: (res) => {
                try {
                  const data = JSON.parse(res.data);
                  if (data.code === 200) {
                    resolve(data.data.url);
                  } else {
                    reject(new Error(data.message || '上传失败'));
                  }
                } catch (e) {
                  reject(new Error('解析响应失败'));
                }
              },
              fail: (err) => {
                reject(err);
              }
            });
          });
        });
        
        return Promise.all(promises);
      };
      
      // 模拟上传图片
      const mockUploadImages = () => {
        // 实际项目中应该调用真实的上传API
        return new Promise(resolve => {
          setTimeout(() => {
            // 模拟图片URL数组
            const imageUrls = this.images.map((_, index) => 
              `https://example.com/uploads/review_${this.orderId}_${index}.jpg`
            );
            resolve(imageUrls);
          }, 1000);
        });
      };
      
      // 开发环境使用模拟上传
      const uploadPromise = process.env.NODE_ENV === 'development' 
        ? mockUploadImages() 
        : uploadImages();
      
      uploadPromise
        .then(imageUrls => {
          // 构建评价数据
          const reviewData = {
            orderId: this.orderId,
            rating: this.rating,
            tags: this.selectedTags,
            content: this.content,
            images: imageUrls,
            isAnonymous: this.isAnonymous
          };
          
          // 提交评价
          return submitOrderReview(reviewData);
        })
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '评价成功',
              icon: 'success'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          } else {
            throw new Error(res.message || '评价提交失败');
          }
        })
        .catch(err => {
          console.error('评价失败', err);
          uni.showToast({
            title: err.message || '评价失败，请重试',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
          this.loading = false;
        });
    }
  }
};
</script>

<style>
.evaluate-container {
  min-height: 100vh;
  background-color: #f6f6f6;
  padding-bottom: 120rpx;
}

/* 导航栏 */
.nav-header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 90rpx;
  background-color: #ffffff;
  padding-top: env(safe-area-inset-top);
}

.back-icon {
  position: absolute;
  left: 30rpx;
  height: 100%;
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 36rpx;
  font-weight: bold;
}

/* 订单卡片 */
.order-card {
  margin: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.order-id {
  font-size: 28rpx;
  color: #666666;
}

.order-status {
  font-size: 28rpx;
  font-weight: bold;
}

.status-5 {
  color: #ff6b00;
}

.order-content {
  padding-top: 20rpx;
}

.address-info {
  margin-bottom: 20rpx;
}

.address-item {
  display: flex;
  margin-bottom: 20rpx;
}

.address-icon {
  width: 40rpx;
  height: 40rpx;
  border-radius: 20rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24rpx;
  color: #ffffff;
  margin-right: 20rpx;
  flex-shrink: 0;
  margin-top: 6rpx;
}

.address-icon.sender {
  background-color: #007aff;
}

.address-icon.receiver {
  background-color: #3cc51f;
}

.address-detail {
  flex: 1;
}

.name-phone {
  display: flex;
  margin-bottom: 10rpx;
}

.name {
  font-size: 28rpx;
  color: #333333;
  margin-right: 20rpx;
}

.phone {
  font-size: 28rpx;
  color: #666666;
}

.address {
  font-size: 28rpx;
  color: #333333;
}

.courier-info {
  display: flex;
  align-items: center;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.courier-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  margin-right: 20rpx;
}

.courier-detail {
  display: flex;
  flex-direction: column;
}

.courier-name {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 8rpx;
}

.courier-desc {
  font-size: 24rpx;
  color: #999999;
}

/* 评分部分 */
.section {
  margin: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 30rpx;
}

.star-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.star-text {
  font-size: 34rpx;
  color: #ff9900;
  margin-bottom: 20rpx;
}

.stars {
  display: flex;
}

/* 标签部分 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
}

.tag-item {
  padding: 16rpx 30rpx;
  background-color: #f5f5f5;
  border-radius: 40rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  color: #666666;
}

.tag-item.active {
  background-color: #e1f7e2;
  color: #3cc51f;
  border: 1rpx solid #3cc51f;
}

/* 评价内容 */
.content-textarea {
  width: 100%;
  height: 200rpx;
  background-color: #f5f5f5;
  border-radius: 6rpx;
  padding: 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.content-counter {
  text-align: right;
  font-size: 24rpx;
  color: #999999;
  margin-top: 10rpx;
}

/* 图片上传 */
.upload-container {
  display: flex;
  flex-wrap: wrap;
}

.image-item {
  position: relative;
  width: 150rpx;
  height: 150rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
}

.preview-image {
  width: 100%;
  height: 100%;
  border-radius: 6rpx;
}

.delete-btn {
  position: absolute;
  right: -10rpx;
  top: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background-color: rgba(0, 0, 0, 0.6);
  border-radius: 20rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload-btn {
  width: 150rpx;
  height: 150rpx;
  border: 1rpx dashed #cccccc;
  border-radius: 6rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 20rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #999999;
  margin-top: 10rpx;
}

.upload-tip {
  font-size: 24rpx;
  color: #999999;
  margin-top: 10rpx;
}

/* 匿名评价 */
.anonymous-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 30rpx;
  color: #333333;
}

.anonymous-tip {
  font-size: 24rpx;
  color: #999999;
  margin-top: 10rpx;
}

/* 提交按钮 */
.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 40rpx;
  background-color: #ffffff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  background-color: #3cc51f;
  color: #ffffff;
  font-size: 32rpx;
  border-radius: 45rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn[disabled] {
  background-color: #cccccc;
}
</style> 