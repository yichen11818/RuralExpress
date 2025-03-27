<template>
  <view class="profile-setup-container">
    <view class="header">
      <text class="title">完善个人信息</text>
      <text class="subtitle">设置您的个人资料，让大家认识您</text>
    </view>
    
    <!-- 头像上传 -->
    <view class="avatar-section">
      <view class="section-title">头像</view>
      <view class="avatar-content">
        <view class="avatar-wrapper">
          <image 
            :src="formData.avatar || '/static/images/default-avatar.png'" 
            class="avatar-image"
            mode="aspectFill"
          ></image>
          <view class="avatar-mask" @click="chooseImage">
            <uni-icons type="camera" size="30" color="#fff"></uni-icons>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 昵称 -->
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input 
          class="form-input" 
          type="text" 
          v-model="formData.nickname" 
          placeholder="请输入昵称"
        />
      </view>
    </view>
    
    <!-- 提交按钮 -->
    <button class="submit-btn" type="primary" @click="saveProfile">保存</button>
    <button class="skip-btn" @click="skipSetup">跳过</button>
  </view>
</template>

<script>
import { getUserInfo } from '@/api/auth';
import { updateUser } from '@/api/user';

export default {
  data() {
    return {
      formData: {
        nickname: '',
        avatar: ''
      }
    };
  },
  
  onLoad(options) {
    // 优先使用传递过来的用户信息参数，减少重复获取
    if (options && options.userInfo) {
      try {
        const userInfo = JSON.parse(decodeURIComponent(options.userInfo));
        this.formData.nickname = userInfo.nickname || '';
        this.formData.avatar = userInfo.avatar || '';
        return;
      } catch (e) {
        console.error('解析userInfo参数失败', e);
      }
    }
    
    // 如果没有传参，再从本地存储获取
    const userInfo = getUserInfo();
    if (userInfo) {
      this.formData.nickname = userInfo.nickname || '';
      this.formData.avatar = userInfo.avatar || '';
    }
  },
  
  methods: {
    // 选择头像图片
    chooseImage() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          
          // 显示加载中
          uni.showLoading({
            title: '处理中...',
            mask: true
          });
          
          // 模拟图片上传
          setTimeout(() => {
            this.formData.avatar = tempFilePath;
            uni.hideLoading();
          }, 500);
          
          // 实际开发中，应该上传图片到服务器
          /*
          const uploadTask = uni.uploadFile({
            url: 'https://your-upload-api.com',
            filePath: tempFilePath,
            name: 'file',
            success: (uploadRes) => {
              const result = JSON.parse(uploadRes.data);
              this.formData.avatar = result.fileUrl;
              uni.hideLoading();
            },
            fail: (err) => {
              uni.hideLoading();
              uni.showToast({
                title: '上传失败',
                icon: 'none'
              });
            }
          });
          */
        }
      });
    },
    
    // 保存个人资料
    saveProfile() {
      // 验证表单
      if (!this.formData.nickname) {
        uni.showToast({
          title: '请输入昵称',
          icon: 'none'
        });
        return;
      }
      
      // 显示加载中
      uni.showLoading({
        title: '保存中...',
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
      
      // 构建更新数据
      const updateData = {
        nickname: this.formData.nickname,
        avatar: this.formData.avatar
      };
      
      // 模拟更新成功
      setTimeout(() => {
        // 更新本地存储的用户信息
        const newUserInfo = {
          ...userInfo,
          ...updateData
        };
        uni.setStorageSync('userInfo', newUserInfo);
        
        uni.hideLoading();
        
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        });
        
        // 跳转到首页
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          });
        }, 1500);
      }, 1000);
      
      // 实际应调用API
      /*
      updateUser(userInfo.id, updateData).then(res => {
        // 更新本地存储的用户信息
        const newUserInfo = {
          ...userInfo,
          ...updateData
        };
        uni.setStorageSync('userInfo', newUserInfo);
        
        uni.hideLoading();
        
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        });
        
        // 跳转到首页
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          });
        }, 1500);
      }).catch(err => {
        uni.hideLoading();
        uni.showToast({
          title: err.message || '保存失败',
          icon: 'none'
        });
      });
      */
    },
    
    // 跳过设置
    skipSetup() {
      uni.showModal({
        title: '提示',
        content: '您可以稍后在个人中心完善资料，确定跳过吗？',
        success: (res) => {
          if (res.confirm) {
            uni.switchTab({
              url: '/pages/index/index'
            });
          }
        }
      });
    }
  }
};
</script>

<style>
.profile-setup-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding: 30rpx;
}

.header {
  margin: 40rpx 0 60rpx;
  text-align: center;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.subtitle {
  font-size: 28rpx;
  color: #666;
}

.section-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.avatar-section {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.avatar-content {
  display: flex;
  justify-content: center;
  padding: 20rpx 0;
}

.avatar-wrapper {
  position: relative;
  width: 180rpx;
  height: 180rpx;
  border-radius: 90rpx;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-mask {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60rpx;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.form-section {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.form-item {
  display: flex;
  align-items: center;
}

.form-label {
  width: 120rpx;
  font-size: 30rpx;
  color: #333;
}

.form-input {
  flex: 1;
  height: 80rpx;
  font-size: 30rpx;
  color: #333;
}

.submit-btn {
  width: 90%;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin: 30rpx auto;
}

.skip-btn {
  width: 90%;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #fff;
  color: #666;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin: 0 auto 40rpx;
}
</style> 