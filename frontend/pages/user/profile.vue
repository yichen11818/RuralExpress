<template>
  <view class="profile-container">
    <!-- 增加顶部状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <view class="profile-header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="page-title">个人资料</text>
      <text class="save-btn" @click="saveProfile">保存</text>
    </view>
    
    <view class="form-section">
      <view class="form-item avatar-item">
        <text class="form-label">头像</text>
        <view class="avatar-wrapper" @click="chooseAvatar">
          <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
          <uni-icons type="camera" size="20" color="#999" class="camera-icon"></uni-icons>
        </view>
      </view>
      
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input class="form-input" v-model="userInfo.nickname" placeholder="请输入昵称" />
      </view>
      
      <view class="form-item">
        <text class="form-label">手机号</text>
        <text class="form-value">{{ formatPhone(userInfo.phone) }}</text>
        <text class="change-btn" @click="changePhone">更换</text>
      </view>
      
      <view class="form-item">
        <text class="form-label">性别</text>
        <picker class="form-picker" @change="bindGenderChange" :value="genderIndex" :range="genderArray">
          <view class="picker-value">{{ genderArray[genderIndex] }}</view>
        </picker>
        <uni-icons type="right" size="16" color="#ccc"></uni-icons>
      </view>
      
      <view class="form-item">
        <text class="form-label">生日</text>
        <picker class="form-picker" mode="date" @change="bindBirthdayChange" :value="userInfo.birthday || '1990-01-01'">
          <view class="picker-value">{{ userInfo.birthday || '请选择' }}</view>
        </picker>
        <uni-icons type="right" size="16" color="#ccc"></uni-icons>
      </view>
    </view>
    
    <view class="tip-text">* 完善个人资料有助于获得更好的服务体验</view>
  </view>
</template>

<script>
import { getUserProfile, updateUserProfile } from '@/api/user';

export default {
  data() {
    return {
      userInfo: {},
      genderArray: ['男', '女', '保密'],
      genderIndex: 2,
      statusBarHeight: 20, // 默认值
      isLoading: false
    };
  },
  
  onLoad() {
    // 获取状态栏高度
    this.getStatusBarHeight();
    
    // 获取用户信息
    this.fetchUserProfile();
  },
  
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      // 获取系统信息
      uni.getSystemInfo({
        success: (res) => {
          this.statusBarHeight = res.statusBarHeight || 20;
        }
      });
    },
    
    goBack() {
      uni.navigateBack();
    },
    
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '****' + phone.substring(7);
    },
    
    // 获取用户资料
    fetchUserProfile() {
      this.isLoading = true;
      
      uni.showLoading({
        title: '加载中...'
      });
      
      getUserProfile()
        .then(res => {
          if (res.code === 200 && res.data) {
            this.userInfo = res.data;
            
            // 设置性别索引
            if (this.userInfo.gender !== undefined) {
              this.genderIndex = this.userInfo.gender;
            }
          } else {
            uni.showToast({
              title: '获取用户信息失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取用户信息失败', err);
          uni.showToast({
            title: '获取用户信息失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
          this.isLoading = false;
        });
    },
    
    // 选择头像
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          
          // 设置临时头像显示
          this.userInfo.avatar = tempFilePath;
          
          // 上传头像到服务器
          this.uploadAvatar(tempFilePath);
        }
      });
    },
    
    // 上传头像
    uploadAvatar(filePath) {
      uni.showLoading({
        title: '上传中...'
      });
      
      // 上传文件
      uni.uploadFile({
        url: 'http://localhost:8080/api/file/upload',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': `Bearer ${uni.getStorageSync('token')}`
        },
        formData: {
          'type': 'avatar'
        },
        success: (uploadRes) => {
          try {
            const response = JSON.parse(uploadRes.data);
            if (response.code === 200 && response.data) {
              // 更新头像URL
              this.userInfo.avatar = response.data.url;
              uni.showToast({
                title: '上传成功',
                icon: 'success'
              });
            } else {
              uni.showToast({
                title: response.message || '上传失败',
                icon: 'none'
              });
            }
          } catch (e) {
            uni.showToast({
              title: '上传失败',
              icon: 'none'
            });
          }
        },
        fail: () => {
          uni.showToast({
            title: '上传失败',
            icon: 'none'
          });
        },
        complete: () => {
          uni.hideLoading();
        }
      });
    },
    
    // 性别选择
    bindGenderChange(e) {
      this.genderIndex = e.detail.value;
      this.userInfo.gender = parseInt(e.detail.value);
    },
    
    // 生日选择
    bindBirthdayChange(e) {
      this.userInfo.birthday = e.detail.value;
    },
    
    // 更换手机号
    changePhone() {
      uni.showToast({
        title: '更换手机号功能开发中',
        icon: 'none'
      });
    },
    
    // 保存资料
    saveProfile() {
      if (this.isLoading) return;
      
      uni.showLoading({
        title: '保存中...'
      });
      
      this.isLoading = true;
      
      updateUserProfile(this.userInfo)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '保存成功',
              icon: 'success'
            });
            
            // 返回上一页
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          } else {
            uni.showToast({
              title: res.message || '保存失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('保存用户信息失败', err);
          uni.showToast({
            title: '保存失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
          this.isLoading = false;
        });
    }
  }
};
</script>

<style>
.profile-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
  /* 不需要顶部padding了，因为我们有状态栏占位元素 */
  /* padding-top: var(--status-bar-height, 0); */
}

/* 状态栏占位 */
.status-bar {
  width: 100%;
  background-color: #fff;
}

.profile-header {
  position: relative;
  padding: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  margin-bottom: 20rpx;
  /* 调整顶部和底部的padding更合理 */
  padding-top: 20rpx;
  padding-bottom: 20rpx;
}

.back-btn {
  position: absolute;
  left: 30rpx;
}

.save-btn {
  position: absolute;
  right: 30rpx;
  font-size: 32rpx;
  color: #3cc51f;
}

.page-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.form-section {
  background-color: #fff;
  padding: 0 30rpx;
  padding-top: 20rpx;
  margin-top: 20rpx;
  border-radius: 12rpx;
  margin-left: 20rpx;
  margin-right: 20rpx;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.form-label {
  width: 160rpx;
  font-size: 30rpx;
  color: #333;
}

.form-input {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.form-value {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.form-picker {
  flex: 1;
}

.picker-value {
  font-size: 30rpx;
  color: #333;
}

.change-btn {
  font-size: 28rpx;
  color: #3cc51f;
}

.avatar-item {
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  overflow: hidden;
}

.avatar {
  width: 100%;
  height: 100%;
}

.camera-icon {
  position: absolute;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.tip-text {
  font-size: 24rpx;
  color: #999;
  padding: 30rpx;
  text-align: center;
  margin-top: 40rpx;
}
</style> 