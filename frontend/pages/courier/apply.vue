<template>
  <view class="apply-container">
    <view class="header">
      <image class="header-bg" src="/static/images/courier-bg.jpg" mode="aspectFill"></image>
      <view class="header-content">
        <text class="title">申请成为快递员</text>
        <text class="subtitle">加入乡递通，一起解决乡村快递最后一公里</text>
      </view>
    </view>
    
    <view class="form-container">
      <!-- 申请要求 -->
      <view class="requirement-section">
        <view class="section-title">申请要求</view>
        <view class="requirements">
          <view class="requirement-item">
            <uni-icons type="checkbox-filled" size="16" color="#3cc51f"></uni-icons>
            <text>年满18周岁，身体健康</text>
          </view>
          <view class="requirement-item">
            <uni-icons type="checkbox-filled" size="16" color="#3cc51f"></uni-icons>
            <text>具备熟悉本地区域的能力</text>
          </view>
          <view class="requirement-item">
            <uni-icons type="checkbox-filled" size="16" color="#3cc51f"></uni-icons>
            <text>拥有智能手机和交通工具</text>
          </view>
          <view class="requirement-item">
            <uni-icons type="checkbox-filled" size="16" color="#3cc51f"></uni-icons>
            <text>良好的服务意识和沟通能力</text>
          </view>
        </view>
      </view>
      
      <!-- 申请表单 -->
      <view class="form-section">
        <view class="section-title">基本信息</view>
        
        <!-- 服务区域 -->
        <view class="form-item">
          <text class="form-label required">服务区域</text>
          <input 
            class="form-input" 
            type="text" 
            placeholder="请输入您的服务区域" 
            v-model="formData.serviceArea"
          />
        </view>
        
        <!-- 工作时间 -->
        <view class="form-item">
          <text class="form-label">工作时间</text>
          <view class="time-picker">
            <picker 
              mode="time" 
              :value="formData.workStartTime" 
              @change="onStartTimeChange"
            >
              <view class="picker-value">{{ formData.workStartTime || '开始时间' }}</view>
            </picker>
            <text class="time-separator">至</text>
            <picker 
              mode="time" 
              :value="formData.workEndTime" 
              @change="onEndTimeChange"
            >
              <view class="picker-value">{{ formData.workEndTime || '结束时间' }}</view>
            </picker>
          </view>
        </view>
        
        <!-- 交通工具 -->
        <view class="form-item">
          <text class="form-label">交通工具</text>
          <picker 
            :range="vehicles" 
            @change="onVehicleChange"
          >
            <view class="picker-value">{{ formData.vehicle || '请选择交通工具' }}</view>
          </picker>
        </view>
        
        <!-- 自我介绍 -->
        <view class="form-item">
          <text class="form-label">自我介绍</text>
          <textarea 
            class="form-textarea" 
            placeholder="请简要介绍一下您自己，例如配送经验等" 
            v-model="formData.introduction"
          ></textarea>
        </view>
      </view>
      
      <!-- 身份证上传 -->
      <view class="form-section">
        <view class="section-title">实名认证</view>
        <text class="section-tips">请上传清晰的身份证照片，用于审核和实名认证</text>
        
        <!-- 身份证正面 -->
        <view class="form-item">
          <text class="form-label required">身份证正面</text>
          <view class="upload-container" @click="chooseImage('front')">
            <image 
              v-if="formData.idCardFront" 
              :src="getPreviewUrl(formData.idCardFront)" 
              mode="aspectFit" 
              class="upload-image"
            ></image>
            <view v-else class="upload-placeholder">
              <uni-icons type="camera-filled" size="24" color="#ddd"></uni-icons>
              <text>点击上传身份证正面</text>
            </view>
          </view>
        </view>
        
        <!-- 身份证背面 -->
        <view class="form-item">
          <text class="form-label required">身份证背面</text>
          <view class="upload-container" @click="chooseImage('back')">
            <image 
              v-if="formData.idCardBack" 
              :src="getPreviewUrl(formData.idCardBack)" 
              mode="aspectFit" 
              class="upload-image"
            ></image>
            <view v-else class="upload-placeholder">
              <uni-icons type="camera-filled" size="24" color="#ddd"></uni-icons>
              <text>点击上传身份证背面</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 提交按钮 -->
      <button class="submit-btn" type="primary" @click="submitApplication">提交申请</button>
      
      <!-- 申请说明 -->
      <view class="apply-notice">
        <text class="notice-text">
          提交申请即表示您同意
          <text class="notice-link" @click="navigateTo('../agreement/courier')">《快递员服务协议》</text>
        </text>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserInfo, isLoggedIn } from '@/api/auth';
import { applyCourier } from '@/api/courier';
import { uploadImage, getPreviewUrl } from '@/api/file';

export default {
  data() {
    return {
      formData: {
        userId: '',
        serviceArea: '',
        workStartTime: '08:00',
        workEndTime: '20:00',
        vehicle: '',
        introduction: '',
        idCardFront: '',
        idCardBack: ''
      },
      vehicles: ['电动车', '摩托车', '自行车', '汽车', '其他']
    };
  },
  
  onLoad() {
    // 检查是否登录
    if (!isLoggedIn()) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateTo({
          url: '/pages/login/login'
        });
      }, 1500);
      return;
    }
    
    // 检查是否实名认证
    const userInfo = getUserInfo();
    if (!userInfo.verified) {
      uni.showModal({
        title: '提示',
        content: '申请成为快递员需要先完成实名认证',
        confirmText: '去认证',
        success: (res) => {
          if (res.confirm) {
            uni.navigateTo({
              url: '/pages/user/verify'
            });
          } else {
            uni.navigateBack();
          }
        }
      });
      return;
    }
    
    // 设置用户ID
    this.formData.userId = userInfo.id;
  },
  
  methods: {
    // 选择图片
    chooseImage(type) {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          
          // 上传图片
          this.uploadImage(tempFilePath, type);
        }
      });
    },
    
    // 上传图片
    uploadImage(filePath, type) {
      uni.showLoading({
        title: '上传中...',
        mask: true
      });
      
      // 上传图片到服务器
      uploadImage(filePath)
        .then(res => {
          if (res.code === 200) {
            // 上传成功，设置图片URL
            const imageUrl = res.data.url;
            if (type === 'front') {
              this.formData.idCardFront = imageUrl;
            } else {
              this.formData.idCardBack = imageUrl;
            }
          } else {
            this.showToast(res.message || '上传失败');
          }
        })
        .catch(err => {
          console.error('上传失败:', err);
          this.showToast('上传失败，请重试');
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 开始时间变化
    onStartTimeChange(e) {
      this.formData.workStartTime = e.detail.value;
    },
    
    // 结束时间变化
    onEndTimeChange(e) {
      this.formData.workEndTime = e.detail.value;
    },
    
    // 交通工具变化
    onVehicleChange(e) {
      this.formData.vehicle = this.vehicles[e.detail.value];
    },
    
    // 提交申请
    submitApplication() {
      // 表单验证
      if (!this.formData.serviceArea) {
        this.showToast('请输入服务区域');
        return;
      }
      
      if (!this.formData.idCardFront) {
        this.showToast('请上传身份证正面照片');
        return;
      }
      
      if (!this.formData.idCardBack) {
        this.showToast('请上传身份证背面照片');
        return;
      }
      
      // 显示提交中
      uni.showLoading({
        title: '提交中...',
        mask: true
      });
      
      // 提交申请
      applyCourier(this.formData)
        .then(res => {
          uni.hideLoading();
          
          uni.showModal({
            title: '申请成功',
            content: '您的快递员申请已提交，我们将尽快审核，请耐心等待',
            showCancel: false,
            success: () => {
              uni.navigateBack();
            }
          });
        })
        .catch(err => {
          uni.hideLoading();
          this.showToast(err.message || '申请失败，请稍后重试');
        });
    },
    
    // 显示提示
    showToast(message) {
      uni.showToast({
        title: message,
        icon: 'none'
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
.apply-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.header {
  position: relative;
  height: 300rpx;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.header-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
}

.title {
  font-size: 40rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.subtitle {
  font-size: 28rpx;
  color: #fff;
}

.form-container {
  padding: 30rpx;
}

.requirement-section, .form-section {
  background-color: #fff;
  border-radius: 8rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f2f2f2;
}

.section-tips {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 20rpx;
}

.requirements {
  margin-top: 20rpx;
}

.requirement-item {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.requirement-item text {
  margin-left: 10rpx;
  font-size: 28rpx;
  color: #666;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.required:after {
  content: '*';
  color: #ff5a5f;
  margin-left: 6rpx;
}

.form-input {
  height: 80rpx;
  background-color: #f8f8f8;
  border-radius: 4rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.form-textarea {
  width: 100%;
  height: 200rpx;
  background-color: #f8f8f8;
  border-radius: 4rpx;
  padding: 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.time-picker {
  display: flex;
  align-items: center;
}

.picker-value {
  height: 80rpx;
  line-height: 80rpx;
  background-color: #f8f8f8;
  border-radius: 4rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  flex: 1;
}

.time-separator {
  margin: 0 20rpx;
  font-size: 28rpx;
  color: #666;
}

.upload-container {
  height: 200rpx;
  background-color: #f8f8f8;
  border-radius: 4rpx;
  overflow: hidden;
}

.upload-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.upload-placeholder text {
  margin-top: 10rpx;
  font-size: 26rpx;
  color: #999;
}

.upload-image {
  width: 100%;
  height: 100%;
}

.submit-btn {
  margin-top: 40rpx;
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  background-color: #3cc51f;
}

.apply-notice {
  margin-top: 30rpx;
  text-align: center;
}

.notice-text {
  font-size: 24rpx;
  color: #999;
}

.notice-link {
  color: #3cc51f;
}
</style> 