<template>
  <view class="container">
    <view class="header">
      <view class="title">系统设置</view>
    </view>
    
    <view class="setting-list" v-if="!loading">
      <view class="setting-group">
        <view class="group-title">基础设置</view>
        <view class="setting-item">
          <text class="setting-label">系统名称</text>
          <input class="setting-input" type="text" v-model="systemSettings.siteName" placeholder="请输入系统名称" />
        </view>
        <view class="setting-item">
          <text class="setting-label">系统LOGO</text>
          <view class="upload-box">
            <image 
              v-if="systemSettings.siteLogo" 
              :src="systemSettings.siteLogo" 
              class="logo-preview" 
              mode="aspectFit"
              @click="previewImage(systemSettings.siteLogo)"
            ></image>
            <view class="upload-btn" @click="uploadImage('siteLogo')">
              <uni-icons type="upload" size="24" color="#999"></uni-icons>
              <text>点击上传</text>
            </view>
            <input class="setting-input" type="text" v-model="systemSettings.siteLogo" placeholder="或输入LOGO图片URL" />
          </view>
        </view>
        <view class="setting-item">
          <text class="setting-label">ICP备案号</text>
          <input class="setting-input" type="text" v-model="systemSettings.icp" placeholder="请输入ICP备案号" />
        </view>
        <view class="setting-item">
          <text class="setting-label">客服电话</text>
          <input class="setting-input" type="text" v-model="systemSettings.servicePhone" placeholder="请输入客服电话" />
        </view>
      </view>
      
      <view class="setting-group">
        <view class="group-title">注册设置</view>
        <view class="setting-item">
          <text class="setting-label">开放注册</text>
          <switch :checked="systemSettings.enableRegister === 1" @change="(e) => systemSettings.enableRegister = e.detail.value ? 1 : 0" />
        </view>
        <view class="setting-item">
          <text class="setting-label">验证码</text>
          <switch :checked="systemSettings.enableCaptcha === 1" @change="(e) => systemSettings.enableCaptcha = e.detail.value ? 1 : 0" />
        </view>
      </view>
      
      <view class="setting-group">
        <view class="group-title">通知设置</view>
        <view class="setting-item">
          <text class="setting-label">短信通知</text>
          <switch :checked="systemSettings.enableSmsNotify === 1" @change="(e) => systemSettings.enableSmsNotify = e.detail.value ? 1 : 0" />
        </view>
        <view class="setting-item">
          <text class="setting-label">邮件通知</text>
          <switch :checked="systemSettings.enableEmailNotify === 1" @change="(e) => systemSettings.enableEmailNotify = e.detail.value ? 1 : 0" />
        </view>
        <view class="setting-item">
          <text class="setting-label">应用内通知</text>
          <switch :checked="systemSettings.enableAppNotify === 1" @change="(e) => systemSettings.enableAppNotify = e.detail.value ? 1 : 0" />
        </view>
      </view>
      
      <view class="setting-group">
        <view class="group-title">其他设置</view>
        <view class="setting-item">
          <text class="setting-label">系统维护模式</text>
          <switch :checked="systemSettings.maintenanceMode === 1" @change="(e) => systemSettings.maintenanceMode = e.detail.value ? 1 : 0" />
        </view>
        <view class="setting-item" v-if="systemSettings.maintenanceMode === 1">
          <text class="setting-label">维护说明</text>
          <textarea class="setting-textarea" v-model="systemSettings.maintenanceMessage" placeholder="请输入系统维护说明信息"></textarea>
        </view>
      </view>
      
      <view class="submit-btn-wrapper">
        <button class="submit-btn" @click="saveSettings">保存设置</button>
      </view>
      
      <!-- 仅在开发环境显示的调试区域 -->
      <view class="debug-area">
        <view class="debug-title">调试区域</view>
        <view class="debug-buttons">
          <button class="debug-btn" @click="loadSettings">重新加载</button>
          <button class="debug-btn" @click="checkConnection">检查网络</button>
          <button class="debug-btn" @click="checkRequestStatus">检查请求</button>
        </view>
      </view>
    </view>
    
    <view class="loading-wrapper" v-else>
      <uni-icons type="spinner-cycle" size="40" color="#007AFF"></uni-icons>
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      systemSettings: {
        siteName: '乡村速递',
        siteLogo: '',
        icp: '',
        servicePhone: '',
        enableRegister: 1,
        enableCaptcha: 1,
        enableSmsNotify: 1,
        enableEmailNotify: 0,
        enableAppNotify: 1,
        maintenanceMode: 0,
        maintenanceMessage: ''
      }
    };
  },
  
  onLoad() {
    console.log('系统设置页面加载 - onLoad');
    this.loadSettings();
  },
  
  methods: {
    // 加载系统设置
    loadSettings() {
      console.log('开始加载系统设置');
      this.loading = true;
      
      // 获取完整请求URL，帮助排查问题
      const requestUrl = 'http://localhost:8080/api/admin/system/settings';
      console.log('发送请求: URL=', requestUrl);
      
      uni.request({
        url: requestUrl,
        method: 'GET',
        header: {
          'Content-Type': 'application/json',
          'Authorization': uni.getStorageSync('token') ? `Bearer ${uni.getStorageSync('token')}` : ''
        },
        success: (res) => {
          console.log('请求成功，响应数据:', res);
          this.loading = false;
          
          if (res.data && res.data.code === 200) {
            console.log('成功获取系统设置数据:', res.data.data);
            this.systemSettings = { ...this.systemSettings, ...res.data.data };
            console.log('合并后的系统设置数据:', this.systemSettings);
          } else {
            console.error('接口返回错误:', res.data);
            uni.showToast({
              title: res.data.message || '获取系统设置失败',
              icon: 'none'
            });
          }
        },
        fail: (err) => {
          console.error('请求失败:', err);
          this.loading = false;
          uni.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
          
          // 显示更多错误信息
          if (err.errMsg) {
            console.error('错误信息:', err.errMsg);
          }
        },
        complete: () => {
          console.log('请求完成，loading状态:', this.loading);
        }
      });
    },
    
    // 保存系统设置
    saveSettings() {
      console.log('开始保存系统设置:', this.systemSettings);
      
      uni.showLoading({
        title: '保存中...'
      });
      
      // 获取完整请求URL
      const requestUrl = 'http://localhost:8080/api/admin/system/settings';
      console.log('发送保存请求: URL=', requestUrl);
      
      uni.request({
        url: requestUrl,
        method: 'POST',
        data: this.systemSettings,
        header: {
          'Content-Type': 'application/json',
          'Authorization': uni.getStorageSync('token') ? `Bearer ${uni.getStorageSync('token')}` : ''
        },
        success: (res) => {
          console.log('保存请求响应:', res);
          uni.hideLoading();
          
          if (res.data && res.data.code === 200) {
            console.log('保存成功');
            uni.showToast({
              title: '保存成功'
            });
          } else {
            console.error('保存失败:', res.data);
            uni.showToast({
              title: res.data.message || '保存失败',
              icon: 'none'
            });
          }
        },
        fail: (err) => {
          console.error('保存请求失败:', err);
          uni.hideLoading();
          uni.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
          
          if (err.errMsg) {
            console.error('错误信息:', err.errMsg);
          }
        }
      });
    },
    
    // 上传图片
    uploadImage(field) {
      uni.chooseImage({
        count: 1,
        success: (chooseImageRes) => {
          const tempFilePaths = chooseImageRes.tempFilePaths;
          
          uni.showLoading({
            title: '上传中...'
          });
          
          uni.uploadFile({
            url: '/api/upload',
            filePath: tempFilePaths[0],
            name: 'file',
            header: {
              'Authorization': uni.getStorageSync('token') ? `Bearer ${uni.getStorageSync('token')}` : ''
            },
            success: (uploadFileRes) => {
              uni.hideLoading();
              try {
                const result = JSON.parse(uploadFileRes.data);
                if (result.code === 200) {
                  this.systemSettings[field] = result.data.url;
                } else {
                  uni.showToast({
                    title: result.message || '上传失败',
                    icon: 'none'
                  });
                }
              } catch (e) {
                uni.showToast({
                  title: '上传失败，返回数据格式错误',
                  icon: 'none'
                });
              }
            },
            fail: () => {
              uni.hideLoading();
              uni.showToast({
                title: '上传失败，请稍后再试',
                icon: 'none'
              });
            }
          });
        }
      });
    },
    
    // 预览图片
    previewImage(url) {
      uni.previewImage({
        urls: [url]
      });
    },
    
    // 调试函数 - 检查网络连接
    checkConnection() {
      console.log('检查网络连接...');
      uni.getNetworkType({
        success: (res) => {
          console.log('当前网络类型:', res.networkType);
        }
      });
    },
    
    // 调试函数 - 检查请求状态
    checkRequestStatus() {
      console.log('请求状态检查...');
      
      const testUrl = 'http://localhost:8080/api/admin/system/settings';
      console.log('测试请求URL:', testUrl);
      
      uni.request({
        url: testUrl,
        method: 'GET',
        header: {
          'Content-Type': 'application/json',
          'Authorization': uni.getStorageSync('token') ? `Bearer ${uni.getStorageSync('token')}` : ''
        },
        success: (res) => {
          console.log('测试请求成功:', res);
        },
        fail: (err) => {
          console.error('测试请求失败:', err);
        }
      });
    }
  }
}
</script>

<style>
.container {
  padding: 30rpx;
  background-color: #f8f8f8;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.setting-list {
  margin-bottom: 40rpx;
}

.setting-group {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx 30rpx;
  margin-bottom: 30rpx;
}

.group-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  margin-bottom: 20rpx;
}

.setting-item {
  display: flex;
  align-items: flex-start;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-label {
  width: 200rpx;
  font-size: 28rpx;
  color: #333;
  line-height: 64rpx;
}

.setting-input {
  flex: 1;
  height: 64rpx;
  border: 1rpx solid #eee;
  border-radius: 6rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.setting-textarea {
  flex: 1;
  height: 160rpx;
  border: 1rpx solid #eee;
  border-radius: 6rpx;
  padding: 20rpx;
  font-size: 28rpx;
  width: 100%;
}

.upload-box {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.logo-preview {
  width: 200rpx;
  height: 200rpx;
  border: 1rpx solid #eee;
  border-radius: 6rpx;
  margin-bottom: 20rpx;
}

.upload-btn {
  width: 200rpx;
  height: 200rpx;
  border: 1rpx dashed #ddd;
  border-radius: 6rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;
  background-color: #f9f9f9;
}

.upload-btn text {
  font-size: 26rpx;
  color: #999;
  margin-top: 10rpx;
}

.submit-btn-wrapper {
  padding: 30rpx 0;
  display: flex;
  justify-content: center;
}

.submit-btn {
  width: 50%;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #007AFF;
  color: #fff;
  font-size: 30rpx;
  border-radius: 40rpx;
}

.loading-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-text {
  font-size: 28rpx;
  color: #999;
  margin-top: 20rpx;
}

.debug-area {
  margin-top: 20rpx;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
}

.debug-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  margin-bottom: 20rpx;
}

.debug-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.debug-btn {
  width: 30%;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #007AFF;
  color: #fff;
  font-size: 30rpx;
  border-radius: 40rpx;
}
</style> 