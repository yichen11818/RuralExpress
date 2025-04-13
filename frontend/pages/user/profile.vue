<template>
  <view class="profile-container">
    <!-- 增加顶部状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <view class="profile-header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="page-title">个人资料</text>
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
        <input class="form-input" v-model="userInfo.nickname" placeholder="请输入昵称" maxlength="20" />
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
        <picker class="form-picker" mode="date" @change="bindBirthdayChange" :value="userInfo.birthday || '1990-01-01'" :end="getCurrentDate()">
          <view class="picker-value">{{ userInfo.birthday || '请选择' }}</view>
        </picker>
        <uni-icons type="right" size="16" color="#ccc"></uni-icons>
      </view>
      
      <view class="form-item">
        <text class="form-label">个性签名</text>
        <textarea class="form-textarea" v-model="userInfo.bio" placeholder="写一句话介绍自己" maxlength="100" />
        <text class="word-count">{{ (userInfo.bio || '').length }}/100</text>
      </view>
    </view>
    
    <view class="tip-text">* 完善个人资料有助于获得更好的服务体验</view>
    
    <!-- 底部保存按钮 -->
    <view class="bottom-btn-wrapper">
      <button class="save-btn-bottom" @click="saveProfile" :disabled="isLoading">{{ isLoading ? '保存中...' : '保存' }}</button>
    </view>
    
    <!-- 更换手机号弹窗 -->
    <uni-popup ref="phonePopup" type="center">
      <view class="popup-content">
        <view class="popup-title">更换手机号</view>
        <view class="popup-form">
          <view class="popup-form-item">
            <input class="popup-input" type="number" maxlength="11" v-model="newPhone" placeholder="请输入新手机号" />
          </view>
          <view class="popup-form-item verify-code-item">
            <input class="popup-input" type="number" maxlength="6" v-model="verifyCode" placeholder="请输入验证码" />
            <button class="verify-btn" :disabled="isSendingCode" @click="sendVerifyCode">
              {{ isSendingCode ? `重新发送(${countdown}s)` : '获取验证码' }}
            </button>
          </view>
        </view>
        <view class="popup-btns">
          <button class="popup-btn cancel-btn" @click="closePhonePopup">取消</button>
          <button class="popup-btn confirm-btn" @click="confirmChangePhone">确认</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { getUserProfile, updateUserProfile, sendPhoneVerifyCode, changePhone } from '@/api/user';
import { updateUserInfo } from '@/api/auth';

export default {
  data() {
    return {
      userInfo: {},
      genderArray: ['男', '女', '保密'],
      genderIndex: 2,
      statusBarHeight: 20, // 默认值
      isLoading: false,
      // 更换手机号相关
      newPhone: '',
      verifyCode: '',
      isSendingCode: false,
      countdown: 60,
      timer: null,
      // 上传相关
      uploadConfig: {
        url: 'http://localhost:8080/api/file/upload',
        type: 'avatar',
        compress: true,
        width: 400,
        height: 400
      }
    };
  },
  
  onLoad() {
    // 获取状态栏高度
    this.getStatusBarHeight();
    
    // 获取用户信息
    this.fetchUserProfile();
  },
  
  onUnload() {
    // 清除倒计时定时器
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  
  methods: {
    // 获取状态栏高度（使用新API）
    getStatusBarHeight() {
      try {
        const res = uni.getWindowInfo();
        this.statusBarHeight = res.statusBarHeight || 20;
      } catch (e) {
        console.error('获取状态栏高度失败', e);
      }
    },
    
    // 获取当前日期字符串（限制生日选择不能超过今天）
    getCurrentDate() {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
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
            
            // 设置默认值
            if (!this.userInfo.bio) {
              this.userInfo.bio = '';
            }
          } else {
            this.showToast('获取用户信息失败');
          }
        })
        .catch(err => {
          console.error('获取用户信息失败', err);
          this.showToast('获取用户信息失败');
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
          
          // 先设置临时头像显示，改善用户体验
          this.userInfo.avatar = tempFilePath;
          
          // 压缩图片
          if (this.uploadConfig.compress) {
            uni.compressImage({
              src: tempFilePath,
              quality: 80,
              width: this.uploadConfig.width,
              height: this.uploadConfig.height,
              success: (compressRes) => {
                this.uploadAvatar(compressRes.tempFilePath);
              },
              fail: () => {
                // 如果压缩失败，使用原图
                this.uploadAvatar(tempFilePath);
              }
            });
          } else {
            this.uploadAvatar(tempFilePath);
          }
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
        url: this.uploadConfig.url,
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': `Bearer ${uni.getStorageSync('token')}`
        },
        formData: {
          'type': this.uploadConfig.type
        },
        success: (uploadRes) => {
          try {
            const response = JSON.parse(uploadRes.data);
            if (response.code === 200 && response.data) {
              // 更新头像URL
              this.userInfo.avatar = response.data.url;
              this.showToast('上传成功', 'success');
            } else {
              this.showToast(response.message || '上传失败');
              // 上传失败，恢复默认头像
              if (!this.userInfo.avatar || this.userInfo.avatar === filePath) {
                this.userInfo.avatar = '/static/images/default-avatar.png';
              }
            }
          } catch (e) {
            this.showToast('上传失败');
            // 上传失败，恢复默认头像
            if (!this.userInfo.avatar || this.userInfo.avatar === filePath) {
              this.userInfo.avatar = '/static/images/default-avatar.png';
            }
          }
        },
        fail: () => {
          this.showToast('上传失败');
          // 上传失败，恢复默认头像
          if (!this.userInfo.avatar || this.userInfo.avatar === filePath) {
            this.userInfo.avatar = '/static/images/default-avatar.png';
          }
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
    
    // 表单验证
    validateForm() {
      if (!this.userInfo.nickname || this.userInfo.nickname.trim() === '') {
        this.showToast('请输入昵称');
        return false;
      }
      
      if (this.userInfo.nickname.length > 20) {
        this.showToast('昵称长度不能超过20个字符');
        return false;
      }
      
      if (this.userInfo.bio && this.userInfo.bio.length > 100) {
        this.showToast('个性签名不能超过100个字符');
        return false;
      }
      
      return true;
    },
    
    // 更换手机号
    changePhone() {
      // 打开更换手机号弹窗
      this.newPhone = '';
      this.verifyCode = '';
      this.$refs.phonePopup.open();
    },
    
    // 关闭手机号弹窗
    closePhonePopup() {
      this.$refs.phonePopup.close();
      // 清除倒计时
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
        this.isSendingCode = false;
      }
    },
    
    // 发送验证码
    sendVerifyCode() {
      // 验证手机号格式
      if (!/^1\d{10}$/.test(this.newPhone)) {
        this.showToast('请输入有效的手机号');
        return;
      }
      
      // 防止重复发送
      if (this.isSendingCode) return;
      
      this.isSendingCode = true;
      
      // 调用发送验证码API
      sendPhoneVerifyCode(this.newPhone)
        .then(res => {
          if (res.code === 200) {
            this.showToast('验证码已发送', 'success');
            
            // 开始倒计时
            this.countdown = 60;
            this.timer = setInterval(() => {
              this.countdown--;
              if (this.countdown <= 0) {
                clearInterval(this.timer);
                this.timer = null;
                this.isSendingCode = false;
              }
            }, 1000);
          } else {
            this.showToast(res.message || '验证码发送失败');
            this.isSendingCode = false;
          }
        })
        .catch(err => {
          console.error('验证码发送失败', err);
          this.showToast('验证码发送失败，请重试');
          this.isSendingCode = false;
        });
    },
    
    // 确认更换手机号
    confirmChangePhone() {
      // 验证手机号和验证码
      if (!/^1\d{10}$/.test(this.newPhone)) {
        this.showToast('请输入有效的手机号');
        return;
      }
      
      if (!this.verifyCode || this.verifyCode.length !== 6) {
        this.showToast('请输入6位验证码');
        return;
      }
      
      // 显示加载
      uni.showLoading({
        title: '提交中...'
      });
      
      // 调用更换手机号API
      changePhone(this.userInfo.id, {
        userId: this.userInfo.id,
        newPhone: this.newPhone,
        verifyCode: this.verifyCode
      })
        .then(res => {
          uni.hideLoading();
          
          if (res.code === 200) {
            // 更新用户手机号
            this.userInfo.phone = this.newPhone;
            this.showToast('手机号更换成功', 'success');
            
            // 更新本地存储中的手机号
            const userInfo = uni.getStorageSync('userInfo') || {};
            userInfo.phone = this.newPhone;
            updateUserInfo(userInfo);
            
            // 关闭弹窗
            this.closePhonePopup();
          } else {
            this.showToast(res.message || '手机号更换失败');
          }
        })
        .catch(err => {
          uni.hideLoading();
          console.error('手机号更换失败', err);
          this.showToast('手机号更换失败，请重试');
        });
    },
    
    // 保存资料
    saveProfile() {
      if (this.isLoading) return;
      
      // 表单验证
      if (!this.validateForm()) return;
      
      uni.showLoading({
        title: '保存中...'
      });
      
      this.isLoading = true;
      
      // 创建用于提交的数据对象
      const profileData = {
        id: this.userInfo.id,
        nickname: this.userInfo.nickname,
        avatar: this.userInfo.avatar,
        gender: this.userInfo.gender,
        birthday: this.userInfo.birthday,
        bio: this.userInfo.bio
      };
      
      updateUserProfile(profileData)
        .then(res => {
          if (res.code === 200) {
            this.showToast('保存成功', 'success');
            
            // 更新本地存储中的用户信息
            const userInfo = uni.getStorageSync('userInfo') || {};
            userInfo.nickname = profileData.nickname;
            userInfo.avatar = profileData.avatar;
            userInfo.gender = profileData.gender;
            userInfo.birthday = profileData.birthday;
            userInfo.bio = profileData.bio;
            // 如果更换了手机号也要更新
            if (this.userInfo.phone !== userInfo.phone) {
              userInfo.phone = this.userInfo.phone;
            }
            
            // 更新本地存储
            updateUserInfo(userInfo);
            
            // 返回上一页
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          } else {
            this.showToast(res.message || '保存失败');
          }
        })
        .catch(err => {
          console.error('保存用户信息失败', err);
          this.showToast('保存失败');
        })
        .finally(() => {
          uni.hideLoading();
          this.isLoading = false;
        });
    },
    
    // 统一提示方法
    showToast(title, icon = 'none') {
      uni.showToast({
        title,
        icon
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

.form-textarea {
  flex: 1;
  font-size: 30rpx;
  color: #333;
  height: 120rpx;
  width: 100%;
}

.word-count {
  position: absolute;
  right: 30rpx;
  bottom: 10rpx;
  font-size: 24rpx;
  color: #999;
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

/* 弹窗样式 */
.popup-content {
  width: 600rpx;
  background-color: #fff;
  border-radius: 12rpx;
  overflow: hidden;
}

.popup-title {
  font-size: 34rpx;
  color: #333;
  text-align: center;
  padding: 30rpx 0;
  font-weight: bold;
  border-bottom: 1rpx solid #f5f5f5;
}

.popup-form {
  padding: 30rpx;
}

.popup-form-item {
  margin-bottom: 30rpx;
}

.popup-input {
  width: 100%;
  height: 80rpx;
  background-color: #f8f8f8;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.verify-code-item {
  display: flex;
  align-items: center;
}

.verify-btn {
  width: 200rpx;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 26rpx;
  color: #fff;
  background-color: #3cc51f;
  margin-left: 20rpx;
  border-radius: 8rpx;
  padding: 0;
}

.verify-btn[disabled] {
  background-color: #ccc;
}

.popup-btns {
  display: flex;
  border-top: 1rpx solid #f5f5f5;
}

.popup-btn {
  flex: 1;
  height: 90rpx;
  line-height: 90rpx;
  text-align: center;
  font-size: 32rpx;
  background-color: #fff;
  border-radius: 0;
}

.popup-btn::after {
  border: none;
}

.cancel-btn {
  color: #666;
  border-right: 1rpx solid #f5f5f5;
}

.confirm-btn {
  color: #3cc51f;
}

.save-btn {
  display: none; /* 隐藏顶部的保存按钮 */
}

/* 底部保存按钮样式 */
.bottom-btn-wrapper {
  padding: 30rpx;
  margin-top: 30rpx;
}

.save-btn-bottom {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  text-align: center;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
  font-weight: bold;
}

.save-btn-bottom[disabled] {
  background-color: #85d47f;
  color: rgba(255, 255, 255, 0.8);
}

.save-btn-bottom:active {
  opacity: 0.8;
}
</style> 