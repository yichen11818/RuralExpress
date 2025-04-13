<template>
  <view class="password-container">
    <!-- 增加顶部状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <view class="password-header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="page-title">修改密码</text>
    </view>
    
    <view class="password-form">
      <view class="form-item">
        <text class="form-label">当前密码</text>
        <input 
          class="form-input" 
          type="password" 
          v-model="formData.oldPassword" 
          placeholder="请输入当前密码"
          password
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">新密码</text>
        <input 
          class="form-input" 
          type="password" 
          v-model="formData.newPassword" 
          placeholder="请输入新密码（6-20位）"
          password
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">确认新密码</text>
        <input 
          class="form-input" 
          type="password" 
          v-model="formData.confirmPassword" 
          placeholder="请再次输入新密码"
          password
        />
      </view>
      
      <view class="password-tips">
        <text>*密码长度为6-20个字符</text>
      </view>
      
      <button class="submit-btn" @click="submitForm">确认修改</button>
    </view>
  </view>
</template>

<script>
import { updatePassword } from '@/api/user';
import { getUserInfo } from '@/api/auth';

export default {
  data() {
    return {
      formData: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      statusBarHeight: 20, // 默认值
      userId: null // 将userId从Vuex移到data中
    };
  },
  
  onLoad() {
    // 获取状态栏高度
    this.getStatusBarHeight();
    
    // 从本地存储获取用户信息
    const userInfo = getUserInfo();
    if (userInfo && userInfo.id) {
      this.userId = userInfo.id;
    }
  },
  
  methods: {
    // 获取状态栏高度（更新为新API）
    getStatusBarHeight() {
      try {
        // 使用新的API获取状态栏高度
        const res = uni.getWindowInfo();
        this.statusBarHeight = res.statusBarHeight || 20;
      } catch (e) {
        // 如果新API不可用，保留默认值
        console.error('获取状态栏高度失败', e);
      }
    },
    
    goBack() {
      uni.navigateBack();
    },
    
    submitForm() {
      // 表单验证
      if (!this.formData.oldPassword) {
        this.showToast('请输入当前密码');
        return;
      }
      
      if (!this.formData.newPassword) {
        this.showToast('请输入新密码');
        return;
      }
      
      if (this.formData.newPassword.length < 6 || this.formData.newPassword.length > 20) {
        this.showToast('新密码长度需为6-20个字符');
        return;
      }
      
      if (this.formData.newPassword !== this.formData.confirmPassword) {
        this.showToast('两次输入的新密码不一致');
        return;
      }
      
      if (this.formData.newPassword === this.formData.oldPassword) {
        this.showToast('新密码不能与旧密码相同');
        return;
      }
      
      // 再次检查userId是否存在
      if (!this.userId) {
        const userInfo = getUserInfo();
        if (userInfo && userInfo.id) {
          this.userId = userInfo.id;
        } else {
          this.showToast('无法获取用户信息，请重新登录');
          setTimeout(() => {
            uni.navigateTo({
              url: '/pages/login/login'
            });
          }, 1500);
          return;
        }
      }
      
      // 显示加载中
      uni.showLoading({
        title: '提交中...'
      });
      
      // 提交修改密码请求
      updatePassword(this.userId, {
        userId: this.userId,
        oldPassword: this.formData.oldPassword,
        newPassword: this.formData.newPassword
      }).then(res => {
        uni.hideLoading();
        if (res.code === 200) {
          uni.showToast({
            title: '密码修改成功',
            icon: 'success',
            duration: 2000
          });
          
          // 延迟返回上一页
          setTimeout(() => {
            uni.navigateBack();
          }, 2000);
        } else {
          this.showToast(res.message || '密码修改失败');
        }
      }).catch(err => {
        uni.hideLoading();
        this.showToast('密码修改失败，请重试');
        console.error(err);
      });
    },
    
    showToast(title) {
      uni.showToast({
        title,
        icon: 'none',
        duration: 2000
      });
    }
  }
};
</script>

<style>
.password-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

/* 状态栏占位 */
.status-bar {
  width: 100%;
  background-color: #fff;
}

.password-header {
  position: relative;
  padding: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  margin-bottom: 20rpx;
  padding-top: 20rpx;
  padding-bottom: 20rpx;
}

.back-btn {
  position: absolute;
  left: 30rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.password-form {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
}

.form-input {
  width: 100%;
  height: 80rpx;
  background-color: #f8f8f8;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.password-tips {
  font-size: 24rpx;
  color: #999;
  margin: 10rpx 0 40rpx;
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  text-align: center;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
  margin-top: 40rpx;
}

.submit-btn:active {
  opacity: 0.8;
}
</style> 