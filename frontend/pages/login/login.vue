<template>
  <view class="login-container">
    <view class="logo-container">
      <image class="logo" src="/static/images/logo.png" mode="aspectFit"></image>
      <text class="app-name">乡递通</text>
      <text class="app-slogan">解决乡村快递最后一公里</text>
    </view>
    
    <view class="form-container">
      <view class="tabs">
        <view 
          class="tab-item" 
          :class="{ active: activeTab === 'login' }" 
          @click="switchTab('login')"
        >
          登录
        </view>
        <view 
          class="tab-item" 
          :class="{ active: activeTab === 'register' }" 
          @click="switchTab('register')"
        >
          注册
        </view>
      </view>
      
      <!-- 登录表单 -->
      <view class="form" v-if="activeTab === 'login'">
        <view class="input-item">
          <input 
            type="text" 
            placeholder="请输入手机号" 
            v-model="loginForm.phone" 
            maxlength="11"
          />
        </view>
        <view class="input-item">
          <input 
            type="password" 
            placeholder="请输入密码" 
            v-model="loginForm.password"
          />
        </view>
        <view class="remember-me">
          <checkbox :checked="loginForm.rememberMe" @click="loginForm.rememberMe = !loginForm.rememberMe" />
          <text>记住我</text>
        </view>
        <button class="submit-btn" type="primary" @click="handleLogin">登录</button>
        
        <view class="agreement">
          <checkbox :checked="loginForm.agreement" @click="loginForm.agreement = !loginForm.agreement" color="#3cc51f" />
          <text class="agreement-text">
            我已阅读并同意
            <text class="agreement-link" @click="navigateTo('../agreement/privacy')">《隐私政策》</text>
            和
            <text class="agreement-link" @click="navigateTo('../agreement/user')">《用户协议》</text>
          </text>
        </view>
      </view>
      
      <!-- 注册表单 -->
      <view class="form" v-if="activeTab === 'register'">
        <view class="input-item">
          <input 
            type="text" 
            placeholder="请输入手机号" 
            v-model="registerForm.phone" 
            maxlength="11"
          />
        </view>
        <view class="input-item">
          <input 
            type="password" 
            placeholder="请输入密码" 
            v-model="registerForm.password"
          />
        </view>
        <button class="submit-btn" type="primary" @click="handleRegister">注册</button>
        
        <view class="agreement">
          <checkbox :checked="registerForm.agreement" @click="registerForm.agreement = !registerForm.agreement" color="#3cc51f" />
          <text class="agreement-text">
            我已阅读并同意
            <text class="agreement-link" @click="navigateTo('../agreement/privacy')">《隐私政策》</text>
            和
            <text class="agreement-link" @click="navigateTo('../agreement/user')">《用户协议》</text>
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { login, register, saveLoginInfo } from '@/api/auth';

export default {
  data() {
    return {
      activeTab: 'login', // 当前激活的标签页
      
      // 登录表单
      loginForm: {
        phone: '',
        password: '',
        rememberMe: false,
        agreement: false
      },
      
      // 注册表单
      registerForm: {
        phone: '',
        password: '',
        agreement: false
      }
    };
  },
  
  methods: {
    // 切换标签页
    switchTab(tab) {
      this.activeTab = tab;
    },
    
    // 处理登录
    async handleLogin() {
      // 表单验证
      if (!this.loginForm.phone) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return;
      }
      
      if (!this.loginForm.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        });
        return;
      }
      
      if (!this.loginForm.agreement) {
        uni.showToast({
          title: '请阅读并同意相关协议',
          icon: 'none'
        });
        return;
      }
      
      try {
        // 显示加载中
        uni.showLoading({
          title: '登录中...',
          mask: true
        });
        
        // 调用登录接口
        const res = await login(this.loginForm);
        
        // 保存登录信息
        saveLoginInfo(res.data);
        
        // 隐藏加载
        uni.hideLoading();
        
        // 判断是否是首次登录（未设置昵称和头像）
        if (!res.data.user.nickname || !res.data.user.avatar) {
          // 跳转到个人信息完善页面，并传递用户信息
          const userInfoParam = encodeURIComponent(JSON.stringify(res.data.user));
          uni.navigateTo({
            url: `../user/profile-setup?userInfo=${userInfoParam}`
          });
        } else {
          // 跳转到首页
          uni.switchTab({
            url: '/pages/index/index'
          });
        }
      } catch (error) {
        // 隐藏加载
        uni.hideLoading();
        console.error('登录失败', error);
      }
    },
    
    // 处理注册
    async handleRegister() {
      // 表单验证
      if (!this.registerForm.phone) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return;
      }
      
      // 验证手机号格式
      if (!/^1[3-9]\d{9}$/.test(this.registerForm.phone)) {
        uni.showToast({
          title: '手机号格式不正确',
          icon: 'none'
        });
        return;
      }
      
      if (!this.registerForm.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        });
        return;
      }
      
      // 验证密码长度
      if (this.registerForm.password.length < 6 || this.registerForm.password.length > 20) {
        uni.showToast({
          title: '密码长度必须在6-20位之间',
          icon: 'none'
        });
        return;
      }
      
      if (!this.registerForm.agreement) {
        uni.showToast({
          title: '请阅读并同意相关协议',
          icon: 'none'
        });
        return;
      }
      
      try {
        // 显示加载中
        uni.showLoading({
          title: '注册中...',
          mask: true
        });
        
        // 调用注册接口
        const res = await register(this.registerForm);
        
        // 隐藏加载
        uni.hideLoading();
        
        // 提示注册成功
        uni.showToast({
          title: '注册成功',
          icon: 'success'
        });
        
        // 调用登录接口，自动登录
        const loginRes = await login({
          phone: this.registerForm.phone,
          password: this.registerForm.password
        });
        
        // 保存登录信息
        saveLoginInfo(loginRes.data);
        
        // 跳转到个人信息完善页面，并传递用户信息
        const userInfoParam = encodeURIComponent(JSON.stringify(loginRes.data.user));
        uni.navigateTo({
          url: `../user/profile-setup?userInfo=${userInfoParam}`
        });
      } catch (error) {
        // 隐藏加载
        uni.hideLoading();
        console.error('注册失败', error);
        
        // 显示后端返回的错误信息
        let errorMsg = '注册失败';
        if (error && error.data && error.data.message) {
          errorMsg = error.data.message;
        }
        
        uni.showToast({
          title: errorMsg,
          icon: 'none',
          duration: 3000
        });
      }
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
.login-container {
  min-height: 100vh;
  padding: 40rpx;
  background-color: #f8f8f8;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-container {
  margin: 80rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo {
  width: 180rpx;
  height: 180rpx;
  margin-bottom: 20rpx;
}

.app-name {
  font-size: 44rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.app-slogan {
  font-size: 28rpx;
  color: #666;
}

.form-container {
  width: 100%;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.tabs {
  display: flex;
  border-bottom: 1rpx solid #eee;
  margin-bottom: 30rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 32rpx;
  color: #666;
}

.tab-item.active {
  color: #3cc51f;
  border-bottom: 4rpx solid #3cc51f;
}

.form {
  padding: 20rpx 0;
}

.input-item {
  border-bottom: 1rpx solid #eee;
  padding: 20rpx 0;
  margin-bottom: 20rpx;
}

.input-item input {
  height: 80rpx;
  font-size: 32rpx;
}

.remember-me {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}

.remember-me text {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
}

.agreement {
  display: flex;
  align-items: flex-start;
  margin-top: 30rpx;
}

.agreement-text {
  font-size: 26rpx;
  color: #666;
  margin-left: 10rpx;
  line-height: 1.5;
}

.agreement-link {
  color: #3cc51f;
}
</style> 