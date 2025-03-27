<template>
  <view class="review-container">
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
          <text class="item-label">收件地址</text>
          <text class="item-value">{{ orderInfo.address }}</text>
        </view>
        <view class="order-item">
          <text class="item-label">收件时间</text>
          <text class="item-value">{{ orderInfo.receivedTime }}</text>
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
            :type="reviewData.rating >= index ? 'star-filled' : 'star'"
            size="36"
            :color="reviewData.rating >= index ? '#ff9900' : '#ccc'"
            @click="setRating(index)"
          ></uni-icons>
        </view>
      </view>
    </view>
    
    <!-- 快递服务评价 -->
    <view class="service-section">
      <view class="section-title">配送服务评价</view>
      <view class="tag-container">
        <view
          v-for="(tag, index) in serviceTags"
          :key="index"
          class="tag-item"
          :class="{ active: reviewData.serviceTags.includes(tag) }"
          @click="toggleServiceTag(tag)"
        >
          {{ tag }}
        </view>
      </view>
    </view>
    
    <!-- 快递员评价 -->
    <view class="courier-section">
      <view class="section-title">快递员服务评价</view>
      <view class="tag-container">
        <view
          v-for="(tag, index) in courierTags"
          :key="index"
          class="tag-item"
          :class="{ active: reviewData.courierTags.includes(tag) }"
          @click="toggleCourierTag(tag)"
        >
          {{ tag }}
        </view>
      </view>
    </view>
    
    <!-- 评价内容 -->
    <view class="comment-section">
      <view class="section-title">评价内容</view>
      <view class="comment-wrapper">
        <textarea
          class="comment-input"
          v-model="reviewData.comment"
          placeholder="请输入您的评价内容，帮助我们提供更好的服务..."
          maxlength="200"
          auto-height
        ></textarea>
        <view class="comment-length">{{ reviewData.comment.length }}/200</view>
      </view>
    </view>
    
    <!-- 上传图片 -->
    <view class="upload-section">
      <view class="section-title">上传图片（选填）</view>
      <view class="upload-container">
        <view class="image-list">
          <view class="image-item" v-for="(item, index) in reviewData.images" :key="index">
            <image :src="item" mode="aspectFill" class="preview-image" @click="previewImage(index)"></image>
            <view class="delete-icon" @click.stop="deleteImage(index)">
              <uni-icons type="closeempty" size="20" color="#fff"></uni-icons>
            </view>
          </view>
          
          <view class="upload-item" v-if="reviewData.images.length < 6" @click="chooseImage">
            <uni-icons type="camera" size="36" color="#999"></uni-icons>
            <text class="upload-text">添加图片</text>
          </view>
        </view>
        <text class="upload-tip">最多上传6张图片</text>
      </view>
    </view>
    
    <!-- 匿名评价 -->
    <view class="anonymous-section">
      <view class="anonymous-option">
        <text>匿名评价</text>
        <switch :checked="reviewData.isAnonymous" @change="toggleAnonymous" color="#3cc51f"></switch>
      </view>
      <text class="anonymous-tip">开启后，评价将不会显示您的个人信息</text>
    </view>
    
    <!-- 提交按钮 -->
    <view class="submit-section">
      <button class="submit-btn" :disabled="!canSubmit" @click="submitReview">提交评价</button>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue';
import { isLoggedIn } from '@/api/auth';

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      orderId: null,
      orderInfo: {
        id: 0,
        company: '顺丰速运',
        trackingNo: 'SF1234567890',
        courierName: '张师傅',
        address: '江西省南昌市青山湖区艾溪湖北路77号',
        receivedTime: '2023-03-21 15:30'
      },
      reviewData: {
        rating: 5,
        serviceTags: [],
        courierTags: [],
        comment: '',
        images: [],
        isAnonymous: false
      },
      ratingTexts: ['很差', '较差', '一般', '较好', '很好'],
      serviceTags: ['包装完好', '物流及时', '配送迅速', '服务周到', '体验好', '价格合理'],
      courierTags: ['态度好', '送货快', '穿戴整洁', '提前联系', '礼貌专业', '严格防疫']
    };
  },
  
  computed: {
    canSubmit() {
      return this.reviewData.rating > 0;
    }
  },
  
  onLoad(options) {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.showModal({
        title: '提示',
        content: '请先登录后再评价',
        showCancel: false,
        success: () => {
          uni.navigateTo({
            url: '/pages/login/login'
          });
        }
      });
      return;
    }
    
    if (options.id) {
      this.orderId = options.id;
      this.loadOrderInfo();
    } else {
      uni.showToast({
        title: '订单ID不存在',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  methods: {
    // 加载订单信息
    loadOrderInfo() {
      // 实际应用中，这里应该调用API获取订单信息
      // 示例：
      /*
      uni.showLoading({
        title: '加载中...'
      });
      
      uni.request({
        url: `https://api.example.com/orders/${this.orderId}`,
        method: 'GET',
        success: (res) => {
          uni.hideLoading();
          if (res.data.success) {
            this.orderInfo = res.data.data;
          } else {
            uni.showToast({
              title: res.data.message || '获取订单信息失败',
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
      
      // 模拟订单信息
      this.orderInfo = {
        id: this.orderId,
        company: '顺丰速运',
        trackingNo: 'SF1234567890',
        courierName: '张师傅',
        address: '江西省南昌市青山湖区艾溪湖北路77号',
        receivedTime: '2023-03-21 15:30'
      };
    },
    
    // 设置评分
    setRating(value) {
      this.reviewData.rating = value;
    },
    
    // 切换服务标签
    toggleServiceTag(tag) {
      const index = this.reviewData.serviceTags.indexOf(tag);
      if (index === -1) {
        this.reviewData.serviceTags.push(tag);
      } else {
        this.reviewData.serviceTags.splice(index, 1);
      }
    },
    
    // 切换快递员标签
    toggleCourierTag(tag) {
      const index = this.reviewData.courierTags.indexOf(tag);
      if (index === -1) {
        this.reviewData.courierTags.push(tag);
      } else {
        this.reviewData.courierTags.splice(index, 1);
      }
    },
    
    // 切换匿名评价
    toggleAnonymous(e) {
      this.reviewData.isAnonymous = e.detail.value;
    },
    
    // 选择图片
    chooseImage() {
      uni.chooseImage({
        count: 6 - this.reviewData.images.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.reviewData.images = [...this.reviewData.images, ...res.tempFilePaths];
        }
      });
    },
    
    // 预览图片
    previewImage(index) {
      uni.previewImage({
        urls: this.reviewData.images,
        current: this.reviewData.images[index]
      });
    },
    
    // 删除图片
    deleteImage(index) {
      this.reviewData.images.splice(index, 1);
    },
    
    // 提交评价
    submitReview() {
      if (!this.canSubmit) return;
      
      uni.showLoading({
        title: '提交中...'
      });
      
      // 上传图片
      if (this.reviewData.images.length > 0) {
        this.uploadImages()
          .then(imageUrls => {
            // 提交评价数据
            this.sendReviewData(imageUrls);
          })
          .catch(() => {
            uni.hideLoading();
            uni.showToast({
              title: '图片上传失败',
              icon: 'none'
            });
          });
      } else {
        // 直接提交评价数据
        this.sendReviewData([]);
      }
    },
    
    // 上传图片
    uploadImages() {
      return new Promise((resolve, reject) => {
        const uploadPromises = this.reviewData.images.map(path => {
          return new Promise((resolveUpload, rejectUpload) => {
            // 实际应用中，这里应该调用API上传图片
            // 示例：
            /*
            uni.uploadFile({
              url: 'https://api.example.com/upload',
              filePath: path,
              name: 'file',
              success: (res) => {
                const data = JSON.parse(res.data);
                if (data.success) {
                  resolveUpload(data.url);
                } else {
                  rejectUpload(new Error(data.message || '上传失败'));
                }
              },
              fail: () => {
                rejectUpload(new Error('上传失败'));
              }
            });
            */
            
            // 模拟上传成功
            setTimeout(() => {
              resolveUpload(path);
            }, 500);
          });
        });
        
        Promise.all(uploadPromises)
          .then(urls => resolve(urls))
          .catch(err => reject(err));
      });
    },
    
    // 发送评价数据
    sendReviewData(imageUrls) {
      // 构建评价数据
      const reviewParams = {
        orderId: this.orderId,
        rating: this.reviewData.rating,
        serviceTags: this.reviewData.serviceTags,
        courierTags: this.reviewData.courierTags,
        comment: this.reviewData.comment,
        images: imageUrls,
        isAnonymous: this.reviewData.isAnonymous
      };
      
      // 实际应用中，这里应该调用API提交评价
      // 示例：
      /*
      uni.request({
        url: 'https://api.example.com/orders/review',
        method: 'POST',
        data: reviewParams,
        success: (res) => {
          uni.hideLoading();
          if (res.data.success) {
            uni.showToast({
              title: '评价成功',
              icon: 'success'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          } else {
            uni.showToast({
              title: res.data.message || '评价失败',
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
      
      // 模拟提交成功
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({
          title: '评价成功',
          icon: 'success'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      }, 1000);
    }
  }
};
</script>

<style>
.review-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 通用section样式 */
.order-info-section,
.rating-section,
.service-section,
.courier-section,
.comment-section,
.upload-section,
.anonymous-section {
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

.tag-item.active {
  background-color: #e6f7ea;
  color: #3cc51f;
  border: 1rpx solid #3cc51f;
}

/* 评价内容样式 */
.comment-wrapper {
  position: relative;
}

.comment-input {
  width: 100%;
  min-height: 200rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.comment-length {
  position: absolute;
  right: 20rpx;
  bottom: 20rpx;
  font-size: 24rpx;
  color: #999;
}

/* 上传图片样式 */
.upload-container {
  display: flex;
  flex-direction: column;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
}

.image-item,
.upload-item {
  width: 150rpx;
  height: 150rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 8rpx;
  overflow: hidden;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.delete-icon {
  position: absolute;
  top: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.5);
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0 0 0 8rpx;
}

.upload-item {
  border: 1rpx dashed #ccc;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
}

.upload-text {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.upload-tip {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

/* 匿名评价样式 */
.anonymous-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15rpx 0;
  font-size: 30rpx;
  color: #333;
}

.anonymous-tip {
  font-size: 24rpx;
  color: #999;
}

/* 提交按钮样式 */
.submit-section {
  padding: 30rpx;
}

.submit-btn {
  background-color: #3cc51f;
  color: #fff;
  height: 90rpx;
  line-height: 90rpx;
  border-radius: 45rpx;
  font-size: 32rpx;
  width: 100%;
}

.submit-btn[disabled] {
  background-color: #ccc;
  color: #fff;
}
</style> 