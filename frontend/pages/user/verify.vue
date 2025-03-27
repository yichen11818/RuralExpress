<template>
  <view class="verify-container">
    <!-- 认证说明 -->
    <view class="verify-notice">
      <view class="notice-title">实名认证说明</view>
      <view class="notice-content">
        <view class="notice-item">1. 请确保上传的证件为本人有效证件</view>
        <view class="notice-item">2. 证件信息仅用于身份验证，我们将严格保密</view>
        <view class="notice-item">3. 认证信息提交后将在1-3个工作日内完成审核</view>
      </view>
    </view>
    
    <!-- 个人信息 -->
    <view class="form-section">
      <view class="section-title">个人信息</view>
      
      <view class="form-content">
        <view class="form-item">
          <text class="form-label">真实姓名</text>
          <input 
            class="form-input" 
            type="text" 
            v-model="formData.realName" 
            placeholder="请输入您的真实姓名"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">身份证号</text>
          <input 
            class="form-input" 
            type="idcard" 
            v-model="formData.idCardNo" 
            placeholder="请输入您的身份证号码"
            maxlength="18"
          />
        </view>
      </view>
    </view>
    
    <!-- 证件照片 -->
    <view class="form-section">
      <view class="section-title">证件照片</view>
      
      <view class="upload-content">
        <!-- 身份证正面 -->
        <view class="upload-item">
          <view class="upload-title">身份证正面</view>
          <view class="upload-area" @click="chooseImage('frontImage')" v-if="!formData.frontImage">
            <uni-icons type="camera" size="40" color="#999"></uni-icons>
            <text class="upload-tip">点击上传</text>
          </view>
          <view class="image-preview" v-else>
            <image :src="formData.frontImage" mode="aspectFit" class="preview-image"></image>
            <view class="delete-icon" @click.stop="deleteImage('frontImage')">
              <uni-icons type="closeempty" size="20" color="#fff"></uni-icons>
            </view>
          </view>
          <text class="upload-desc">请上传清晰的身份证人像面</text>
        </view>
        
        <!-- 身份证反面 -->
        <view class="upload-item">
          <view class="upload-title">身份证反面</view>
          <view class="upload-area" @click="chooseImage('backImage')" v-if="!formData.backImage">
            <uni-icons type="camera" size="40" color="#999"></uni-icons>
            <text class="upload-tip">点击上传</text>
          </view>
          <view class="image-preview" v-else>
            <image :src="formData.backImage" mode="aspectFit" class="preview-image"></image>
            <view class="delete-icon" @click.stop="deleteImage('backImage')">
              <uni-icons type="closeempty" size="20" color="#fff"></uni-icons>
            </view>
          </view>
          <text class="upload-desc">请上传清晰的身份证国徽面</text>
        </view>
        
        <!-- 手持身份证 -->
        <view class="upload-item">
          <view class="upload-title">手持身份证照片</view>
          <view class="upload-area" @click="chooseImage('holdingImage')" v-if="!formData.holdingImage">
            <uni-icons type="camera" size="40" color="#999"></uni-icons>
            <text class="upload-tip">点击上传</text>
          </view>
          <view class="image-preview" v-else>
            <image :src="formData.holdingImage" mode="aspectFit" class="preview-image"></image>
            <view class="delete-icon" @click.stop="deleteImage('holdingImage')">
              <uni-icons type="closeempty" size="20" color="#fff"></uni-icons>
            </view>
          </view>
          <text class="upload-desc">请上传本人手持身份证的照片</text>
        </view>
      </view>
    </view>
    
    <!-- 用户协议 -->
    <view class="agreement-section">
      <checkbox 
        :checked="formData.agreement" 
        @click="formData.agreement = !formData.agreement" 
        color="#3cc51f"
      />
      <view class="agreement-text">
        <text>我已阅读并同意</text>
        <text class="agreement-link" @click="navigateTo('/pages/agreement/privacy')">《隐私政策》</text>
        <text>和</text>
        <text class="agreement-link" @click="navigateTo('/pages/agreement/user')">《用户协议》</text>
      </view>
    </view>
    
    <!-- 提交按钮 -->
    <button 
      class="submit-btn" 
      type="primary" 
      @click="submitVerification"
      :disabled="!formData.agreement"
    >
      提交认证
    </button>
    
    <!-- 帮助提示 -->
    <view class="help-tips">
      <text class="help-text">认证过程遇到问题？</text>
      <text class="help-link" @click="contactService">联系客服</text>
    </view>
  </view>
</template>

<script>
import { isLoggedIn, getUserInfo } from '@/api/auth';
import { verifyUser } from '@/api/user';
import { uploadImage } from '@/api/file';

export default {
  data() {
    return {
      // 表单数据
      formData: {
        realName: '',
        idCardNo: '',
        frontImage: '',
        backImage: '',
        holdingImage: '',
        agreement: false
      },
      // 图片上传后的URL
      uploadedImages: {
        frontImage: '',
        backImage: '',
        holdingImage: ''
      }
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
    
    // 获取用户信息
    const userInfo = getUserInfo();
    
    // 检查是否已认证
    if (userInfo && userInfo.verified) {
      uni.showModal({
        title: '提示',
        content: '您已完成实名认证',
        showCancel: false,
        success: () => {
          uni.navigateBack();
        }
      });
    }
  },
  
  methods: {
    // 选择图片
    chooseImage(imageType) {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          
          // 显示临时预览图
          this.formData[imageType] = tempFilePath;
          
          // 上传图片到服务器
          this.uploadImageToServer(tempFilePath, imageType);
        }
      });
    },
    
    // 上传图片到服务器
    uploadImageToServer(filePath, imageType) {
      uni.showLoading({
        title: '上传中...',
        mask: true
      });
      
      // 上传文件
      uni.uploadFile({
        url: 'http://localhost:8080/api/file/upload/image',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': `Bearer ${uni.getStorageSync('token')}`
        },
        success: (uploadResult) => {
          // 解析返回结果
          const result = JSON.parse(uploadResult.data);
          
          if (result.code === 0 && result.data) {
            // 保存上传后的图片URL
            this.uploadedImages[imageType] = result.data.url;
            uni.hideLoading();
          } else {
            uni.hideLoading();
            uni.showToast({
              title: result.msg || '上传失败',
              icon: 'none'
            });
            // 清除预览
            this.formData[imageType] = '';
          }
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({
            title: '网络异常，上传失败',
            icon: 'none'
          });
          // 清除预览
          this.formData[imageType] = '';
        }
      });
    },
    
    // 删除图片
    deleteImage(imageType) {
      uni.showModal({
        title: '提示',
        content: '确定要删除该照片吗？',
        success: (res) => {
          if (res.confirm) {
            // 如果已上传到服务器，删除服务器图片
            if (this.uploadedImages[imageType]) {
              // 这里可以调用deleteFile API删除服务器图片
              this.uploadedImages[imageType] = '';
            }
            // 清除本地预览
            this.formData[imageType] = '';
          }
        }
      });
    },
    
    // 表单验证
    validateForm() {
      if (!this.formData.realName) {
        uni.showToast({
          title: '请输入真实姓名',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.idCardNo) {
        uni.showToast({
          title: '请输入身份证号码',
          icon: 'none'
        });
        return false;
      }
      
      // 简单的身份证号验证
      if (!/^\d{17}[\dXx]$/.test(this.formData.idCardNo)) {
        uni.showToast({
          title: '身份证号码格式不正确',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.frontImage || !this.uploadedImages.frontImage) {
        uni.showToast({
          title: '请上传身份证正面照片',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.backImage || !this.uploadedImages.backImage) {
        uni.showToast({
          title: '请上传身份证反面照片',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.holdingImage || !this.uploadedImages.holdingImage) {
        uni.showToast({
          title: '请上传手持身份证照片',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.agreement) {
        uni.showToast({
          title: '请阅读并同意相关协议',
          icon: 'none'
        });
        return false;
      }
      
      return true;
    },
    
    // 提交认证
    submitVerification() {
      if (!this.validateForm()) {
        return;
      }
      
      // 显示加载中
      uni.showLoading({
        title: '提交中...',
        mask: true
      });
      
      // 获取用户信息
      const userInfo = getUserInfo();
      
      if (!userInfo || !userInfo.id) {
        uni.hideLoading();
        uni.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        });
        return;
      }
      
      // 构建认证数据
      const verifyData = {
        realName: this.formData.realName,
        idCard: this.formData.idCardNo,
        frontImage: this.uploadedImages.frontImage,
        backImage: this.uploadedImages.backImage,
        holdingImage: this.uploadedImages.holdingImage
      };
      
      // 调用认证接口
      verifyUser(userInfo.id, verifyData).then(res => {
        uni.hideLoading();
        uni.showModal({
          title: '提交成功',
          content: '您的实名认证信息已提交，我们将在1-3个工作日内完成审核',
          showCancel: false,
          success: () => {
            uni.navigateBack();
          }
        });
      }).catch(err => {
        uni.hideLoading();
        uni.showToast({
          title: err.message || '提交失败',
          icon: 'none'
        });
      });
    },
    
    // 联系客服
    contactService() {
      uni.makePhoneCall({
        phoneNumber: '4008007001',
        fail: () => {
          uni.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    },
    
    // 页面导航
    navigateTo(url) {
      uni.navigateTo({
        url
      });
    }
  }
};
</script>

<style>
.verify-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding: 20rpx;
  padding-bottom: 80rpx;
}

.verify-notice {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.notice-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.notice-content {
  padding-left: 20rpx;
}

.notice-item {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10rpx;
}

.form-section {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.form-content {
  padding: 0 30rpx;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.form-item:last-child {
  border-bottom: none;
}

.form-label {
  width: 180rpx;
  font-size: 28rpx;
  color: #333;
}

.form-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.upload-content {
  padding: 30rpx;
  display: flex;
  flex-direction: column;
}

.upload-item {
  margin-bottom: 40rpx;
}

.upload-item:last-child {
  margin-bottom: 0;
}

.upload-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
}

.upload-area {
  width: 100%;
  height: 300rpx;
  border: 2rpx dashed #ddd;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f9f9f9;
  margin-bottom: 10rpx;
}

.upload-tip {
  font-size: 28rpx;
  color: #999;
  margin-top: 20rpx;
}

.upload-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.image-preview {
  position: relative;
  width: 100%;
  height: 300rpx;
  margin-bottom: 10rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.delete-icon {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  width: 50rpx;
  height: 50rpx;
  border-radius: 25rpx;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.agreement-section {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
}

.agreement-text {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

.agreement-link {
  color: #3cc51f;
}

.submit-btn {
  width: 90%;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin: 40rpx auto;
}

.submit-btn[disabled] {
  background-color: #ccc;
  color: #fff;
}

.help-tips {
  text-align: center;
  padding: 20rpx 0;
}

.help-text {
  font-size: 28rpx;
  color: #666;
}

.help-link {
  font-size: 28rpx;
  color: #3cc51f;
  margin-left: 10rpx;
}
</style> 