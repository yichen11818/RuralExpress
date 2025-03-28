<template>
  <view class="feedback-container">
    <view class="feedback-header">
      <image class="feedback-banner" src="/static/images/feedback-banner.png" mode="aspectFill"></image>
      <text class="header-title">意见反馈</text>
    </view>
    
    <view class="feedback-content">
      <form @submit="submitFeedback">
        <view class="form-group">
          <text class="form-label">反馈类型</text>
          <view class="type-selector">
            <view 
              class="type-item" 
              v-for="(item, index) in feedbackTypes" 
              :key="index"
              :class="{ active: currentType === index }"
              @click="selectType(index)"
            >
              <text>{{ item }}</text>
            </view>
          </view>
        </view>
        
        <view class="form-group">
          <text class="form-label">反馈内容</text>
          <textarea 
            class="feedback-textarea" 
            v-model="content" 
            placeholder="请详细描述您遇到的问题或建议..."
            placeholder-style="color: #999;"
            :maxlength="500"
          ></textarea>
          <text class="word-count">{{ content.length }}/500</text>
        </view>
        
        <view class="form-group">
          <text class="form-label">上传截图（选填）</text>
          <view class="image-uploader">
            <view 
              class="image-item"
              v-for="(item, index) in images"
              :key="index"
            >
              <image 
                class="preview-image" 
                :src="item" 
                mode="aspectFill"
              ></image>
              <text class="delete-icon" @click="deleteImage(index)">×</text>
            </view>
            
            <view 
              class="upload-btn"
              v-if="images.length < 3"
              @click="chooseImage"
            >
              <text class="upload-icon">+</text>
              <text class="upload-text">上传图片</text>
            </view>
          </view>
          <text class="image-tip">最多上传3张图片，每张不超过2MB</text>
        </view>
        
        <view class="form-group">
          <text class="form-label">联系方式（选填）</text>
          <input 
            class="contact-input" 
            v-model="contact" 
            placeholder="请留下您的手机号/邮箱，方便我们联系您"
            placeholder-style="color: #999;"
          />
        </view>
        
        <button class="submit-btn" form-type="submit" :disabled="!isValid">提交反馈</button>
      </form>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      // 反馈类型
      feedbackTypes: ['功能问题', '体验建议', '投诉举报', '其他'],
      currentType: 0,
      
      // 反馈内容
      content: '',
      
      // 图片列表
      images: [],
      
      // 联系方式
      contact: ''
    };
  },
  
  computed: {
    // 表单验证
    isValid() {
      return this.content.trim().length > 0;
    }
  },
  
  methods: {
    // 选择反馈类型
    selectType(index) {
      this.currentType = index;
    },
    
    // 选择图片
    chooseImage() {
      uni.chooseImage({
        count: 3 - this.images.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          // 处理选择的图片
          const tempFilePaths = res.tempFilePaths;
          this.images = [...this.images, ...tempFilePaths];
        }
      });
    },
    
    // 删除图片
    deleteImage(index) {
      this.images.splice(index, 1);
    },
    
    // 提交反馈
    submitFeedback() {
      if (!this.isValid) {
        uni.showToast({
          title: '请填写反馈内容',
          icon: 'none'
        });
        return;
      }
      
      // 显示加载提示
      uni.showLoading({
        title: '提交中...'
      });
      
      // 构建提交数据
      const feedback = {
        type: this.feedbackTypes[this.currentType],
        content: this.content,
        contact: this.contact,
        // 实际项目中这里应该上传图片并获取链接
        images: this.images
      };
      
      // 模拟提交
      setTimeout(() => {
        console.log('提交的反馈数据:', feedback);
        
        // 关闭加载提示
        uni.hideLoading();
        
        // 提交成功提示
        uni.showToast({
          title: '感谢您的反馈！',
          icon: 'success',
          success: () => {
            // 延迟返回
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        });
      }, 1500);
    }
  }
};
</script>

<style>
.feedback-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 30rpx;
}
  
.feedback-header {
  position: relative;
  height: 200rpx;
  overflow: hidden;
}
    
.feedback-banner {
  width: 100%;
  height: 200rpx;
}
    
.header-title {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}
  
.feedback-content {
  padding: 30rpx;
}
    
.form-group {
  margin-bottom: 30rpx;
}
      
.form-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  font-weight: bold;
}
      
.type-selector {
  display: flex;
  flex-wrap: wrap;
}
        
.type-item {
  width: calc(50% - 20rpx);
  height: 80rpx;
  background-color: #fff;
  border-radius: 8rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #666;
  border: 1px solid #eee;
}
          
.type-item:nth-child(2n) {
  margin-right: 0;
}
          
.type-item.active {
  border-color: #FF6B00;
  color: #FF6B00;
  background-color: rgba(255, 107, 0, 0.05);
}
      
.feedback-textarea {
  width: 100%;
  height: 300rpx;
  background-color: #fff;
  border-radius: 8rpx;
  padding: 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #333;
  border: 1px solid #eee;
}
      
.word-count {
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}
      
.image-uploader {
  display: flex;
  flex-wrap: wrap;
}
        
.image-item {
  width: 200rpx;
  height: 200rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  position: relative;
}
          
.preview-image {
  width: 100%;
  height: 100%;
  border-radius: 8rpx;
}
          
.delete-icon {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background-color: rgba(0, 0, 0, 0.5);
  color: #fff;
  border-radius: 50%;
  text-align: center;
  line-height: 40rpx;
  font-size: 28rpx;
}
        
.upload-btn {
  width: 200rpx;
  height: 200rpx;
  background-color: #fff;
  border: 1px dashed #ddd;
  border-radius: 8rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
          
.upload-icon {
  font-size: 48rpx;
  color: #999;
  margin-bottom: 10rpx;
}
          
.upload-text {
  font-size: 24rpx;
  color: #999;
}
      
.image-tip {
  font-size: 24rpx;
  color: #999;
}
      
.contact-input {
  width: 100%;
  height: 80rpx;
  background-color: #fff;
  border-radius: 8rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #333;
  border: 1px solid #eee;
}
    
.submit-btn {
  width: 100%;
  height: 88rpx;
  background-color: #FF6B00;
  color: #fff;
  font-size: 32rpx;
  border-radius: 44rpx;
  margin-top: 40rpx;
}
      
.submit-btn:disabled {
  background-color: #ccc;
}
</style> 