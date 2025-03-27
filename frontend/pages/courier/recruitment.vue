<template>
  <view class="recruitment-container">
    <!-- 顶部标题 -->
    <view class="header">
      <view class="title">快递员招募</view>
      <view class="subtitle">加入我们，共同服务乡村物流</view>
    </view>
    
    <!-- 收益介绍 -->
    <view class="section income-section">
      <view class="section-title">收益与福利</view>
      <view class="income-list">
        <view class="income-item">
          <view class="income-icon">💰</view>
          <view class="income-info">
            <view class="income-title">高额收入</view>
            <view class="income-desc">每单收益5-10元，月入可达3000+</view>
          </view>
        </view>
        <view class="income-item">
          <view class="income-icon">⏱️</view>
          <view class="income-info">
            <view class="income-title">时间自由</view>
            <view class="income-desc">自由安排工作时间，兼职也可以</view>
          </view>
        </view>
        <view class="income-item">
          <view class="income-icon">🏠</view>
          <view class="income-info">
            <view class="income-title">就近服务</view>
            <view class="income-desc">在自己熟悉的区域工作，无需远行</view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 申请条件 -->
    <view class="section requirements-section">
      <view class="section-title">申请条件</view>
      <view class="requirement-list">
        <view class="requirement-item">
          <text class="requirement-dot"></text>
          <text class="requirement-text">年龄18-55周岁，身体健康</text>
        </view>
        <view class="requirement-item">
          <text class="requirement-dot"></text>
          <text class="requirement-text">无犯罪记录，品行良好</text>
        </view>
        <view class="requirement-item">
          <text class="requirement-dot"></text>
          <text class="requirement-text">熟悉当地社区环境</text>
        </view>
        <view class="requirement-item">
          <text class="requirement-dot"></text>
          <text class="requirement-text">有交通工具（电动车/摩托车等）</text>
        </view>
        <view class="requirement-item">
          <text class="requirement-dot"></text>
          <text class="requirement-text">需提供身份证及相关证明材料</text>
        </view>
      </view>
    </view>
    
    <!-- 申请流程 -->
    <view class="section process-section">
      <view class="section-title">申请流程</view>
      <view class="process-list">
        <view class="process-item">
          <view class="process-step">1</view>
          <view class="process-info">
            <view class="process-title">提交申请</view>
            <view class="process-desc">填写个人信息及工作区域</view>
          </view>
        </view>
        <view class="process-item">
          <view class="process-step">2</view>
          <view class="process-info">
            <view class="process-title">资料审核</view>
            <view class="process-desc">提交身份证及相关证明</view>
          </view>
        </view>
        <view class="process-item">
          <view class="process-step">3</view>
          <view class="process-info">
            <view class="process-title">线上培训</view>
            <view class="process-desc">学习配送规则及系统使用</view>
          </view>
        </view>
        <view class="process-item">
          <view class="process-step">4</view>
          <view class="process-info">
            <view class="process-title">账号激活</view>
            <view class="process-desc">审核通过后即可接单</view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 申请表单 -->
    <view class="section form-section">
      <view class="section-title">申请表单</view>
      <view class="form-item">
        <text class="form-label">姓名</text>
        <input type="text" v-model="formData.name" placeholder="请输入真实姓名" class="form-input" />
      </view>
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input type="number" v-model="formData.phone" placeholder="请输入手机号码" class="form-input" />
      </view>
      <view class="form-item">
        <text class="form-label">身份证号</text>
        <input type="text" v-model="formData.idCard" placeholder="请输入身份证号码" class="form-input" />
      </view>
      <view class="form-item">
        <text class="form-label">服务区域</text>
        <picker mode="region" @change="regionChange" class="form-picker">
          <view class="picker-text" v-if="formData.region">
            {{formData.region[0]}} {{formData.region[1]}} {{formData.region[2]}}
          </view>
          <view class="picker-placeholder" v-else>请选择服务区域</view>
        </picker>
      </view>
      <view class="form-item">
        <text class="form-label">详细地址</text>
        <input type="text" v-model="formData.address" placeholder="请输入详细地址" class="form-input" />
      </view>
      <view class="form-item">
        <text class="form-label">交通工具</text>
        <picker :range="vehicleOptions" @change="vehicleChange" class="form-picker">
          <view class="picker-text" v-if="formData.vehicle">
            {{formData.vehicle}}
          </view>
          <view class="picker-placeholder" v-else>请选择交通工具</view>
        </picker>
      </view>
      
      <view class="id-card-section">
        <text class="form-label">身份证上传</text>
        <view class="id-card-upload">
          <view class="id-card-item" @click="chooseImage('front')">
            <image v-if="formData.idCardFront" :src="formData.idCardFront" mode="aspectFill" class="id-card-image"></image>
            <view v-else class="id-card-placeholder">
              <uni-icons type="camera" size="30" color="#999"></uni-icons>
              <text>身份证正面</text>
            </view>
          </view>
          <view class="id-card-item" @click="chooseImage('back')">
            <image v-if="formData.idCardBack" :src="formData.idCardBack" mode="aspectFill" class="id-card-image"></image>
            <view v-else class="id-card-placeholder">
              <uni-icons type="camera" size="30" color="#999"></uni-icons>
              <text>身份证反面</text>
            </view>
          </view>
        </view>
      </view>
      
      <view class="agreement">
        <checkbox-group @change="agreementChange">
          <label class="agreement-label">
            <checkbox :checked="formData.agreement" color="#3cc51f" />
            <text class="agreement-text">我已阅读并同意《快递员服务协议》</text>
          </label>
        </checkbox-group>
      </view>
      
      <button class="submit-btn" @click="submitApplication" :disabled="!formData.agreement || submitting">
        {{ submitting ? '提交中...' : '提交申请' }}
      </button>
    </view>
    
    <!-- 底部联系方式 -->
    <view class="contact-section">
      <view class="contact-title">有疑问?</view>
      <view class="contact-info">客服电话: 400-888-8888</view>
      <view class="contact-info">工作时间: 周一至周日 8:00-20:00</view>
    </view>
  </view>
</template>

<script>
import uniIcons from '../../uni_modules/uni-icons/components/uni-icons/uni-icons.vue'
import { isLoggedIn } from '@/api/auth';
import { getUserProfile } from '@/api/user';
import { applyCourier } from '@/api/courier';

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      submitting: false,
      loading: false,
      vehicleOptions: ['电动车', '摩托车', '三轮车', '小汽车', '其他'],
      formData: {
        name: '',
        phone: '',
        idCard: '',
        region: null,
        address: '',
        vehicle: '',
        idCardFront: '',
        idCardBack: '',
        agreement: false
      }
    };
  },
  
  onLoad() {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.showModal({
        title: '提示',
        content: '请先登录后再申请成为快递员',
        showCancel: false,
        success: () => {
          uni.navigateTo({
            url: '/pages/login/login'
          });
        }
      });
      return;
    }
    
    // 获取登录用户信息并预填表单
    this.loadUserProfile();
  },
  
  methods: {
    // 加载用户信息并预填表单
    loadUserProfile() {
      this.loading = true;
      
      uni.showLoading({
        title: '加载中...'
      });
      
      getUserProfile()
        .then(res => {
          if (res.code === 200 && res.data) {
            // 预填表单数据
            const userData = res.data;
            this.formData.name = userData.name || userData.nickname || '';
            this.formData.phone = userData.phone || '';
            
            // 如果有默认地址，设置地址信息
            if (userData.defaultAddress) {
              const address = userData.defaultAddress;
              // 尝试解析地址的省市区
              if (address.province && address.city && address.district) {
                this.formData.region = [address.province, address.city, address.district];
              }
              this.formData.address = address.detailAddress || '';
            }
          }
        })
        .catch(err => {
          console.error('获取用户信息失败', err);
        })
        .finally(() => {
          uni.hideLoading();
          this.loading = false;
        });
    },
    
    // 选择服务区域
    regionChange(e) {
      this.formData.region = e.detail.value;
    },
    
    // 选择交通工具
    vehicleChange(e) {
      this.formData.vehicle = this.vehicleOptions[e.detail.value];
    },
    
    // 选择身份证图片
    chooseImage(type) {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          if (type === 'front') {
            this.formData.idCardFront = res.tempFilePaths[0];
          } else {
            this.formData.idCardBack = res.tempFilePaths[0];
          }
        }
      });
    },
    
    // 协议勾选
    agreementChange(e) {
      this.formData.agreement = e.detail.value.length > 0;
    },
    
    // 验证表单
    validateForm() {
      if (!this.formData.name) {
        uni.showToast({
          title: '请输入姓名',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.phone) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return false;
      }
      
      if (!/^1\d{10}$/.test(this.formData.phone)) {
        uni.showToast({
          title: '手机号格式不正确',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.idCard) {
        uni.showToast({
          title: '请输入身份证号',
          icon: 'none'
        });
        return false;
      }
      
      // 身份证号验证 (简化版)
      if (!/^\d{17}[\dXx]$/.test(this.formData.idCard)) {
        uni.showToast({
          title: '身份证号格式不正确',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.region) {
        uni.showToast({
          title: '请选择服务区域',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.address) {
        uni.showToast({
          title: '请输入详细地址',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.vehicle) {
        uni.showToast({
          title: '请选择交通工具',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.idCardFront) {
        uni.showToast({
          title: '请上传身份证正面照片',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.idCardBack) {
        uni.showToast({
          title: '请上传身份证反面照片',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.agreement) {
        uni.showToast({
          title: '请阅读并同意服务协议',
          icon: 'none'
        });
        return false;
      }
      
      return true;
    },
    
    // 上传身份证图片
    uploadIdCardImages() {
      return new Promise((resolve, reject) => {
        const uploadTasks = [];
        
        // 上传身份证正面
        uploadTasks.push(
          new Promise((innerResolve, innerReject) => {
            uni.uploadFile({
              url: 'http://localhost:8080/api/file/upload',
              filePath: this.formData.idCardFront,
              name: 'file',
              header: {
                'Authorization': `Bearer ${uni.getStorageSync('token')}`
              },
              formData: {
                'type': 'idcard'
              },
              success: (uploadRes) => {
                try {
                  const response = JSON.parse(uploadRes.data);
                  if (response.code === 200 && response.data) {
                    innerResolve(response.data.url);
                  } else {
                    innerReject(new Error(response.message || '上传身份证正面失败'));
                  }
                } catch (e) {
                  innerReject(new Error('上传身份证正面失败'));
                }
              },
              fail: () => {
                innerReject(new Error('上传身份证正面失败'));
              }
            });
          })
        );
        
        // 上传身份证反面
        uploadTasks.push(
          new Promise((innerResolve, innerReject) => {
            uni.uploadFile({
              url: 'http://localhost:8080/api/file/upload',
              filePath: this.formData.idCardBack,
              name: 'file',
              header: {
                'Authorization': `Bearer ${uni.getStorageSync('token')}`
              },
              formData: {
                'type': 'idcard'
              },
              success: (uploadRes) => {
                try {
                  const response = JSON.parse(uploadRes.data);
                  if (response.code === 200 && response.data) {
                    innerResolve(response.data.url);
                  } else {
                    innerReject(new Error(response.message || '上传身份证反面失败'));
                  }
                } catch (e) {
                  innerReject(new Error('上传身份证反面失败'));
                }
              },
              fail: () => {
                innerReject(new Error('上传身份证反面失败'));
              }
            });
          })
        );
        
        Promise.all(uploadTasks)
          .then(results => {
            resolve({
              idCardFront: results[0],
              idCardBack: results[1]
            });
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    
    // 提交申请
    submitApplication() {
      if (this.submitting) return;
      
      if (!this.validateForm()) {
        return;
      }
      
      this.submitting = true;
      
      uni.showLoading({
        title: '提交中...',
        mask: true
      });
      
      // 先上传身份证照片
      this.uploadIdCardImages()
        .then(idCardUrls => {
          // 准备提交的数据
          const courierData = {
            name: this.formData.name,
            phone: this.formData.phone,
            idCard: this.formData.idCard,
            province: this.formData.region[0],
            city: this.formData.region[1],
            district: this.formData.region[2],
            address: this.formData.address,
            vehicle: this.formData.vehicle,
            idCardFrontUrl: idCardUrls.idCardFront,
            idCardBackUrl: idCardUrls.idCardBack
          };
          
          // 调用API提交申请
          return applyCourier(courierData);
        })
        .then(res => {
          if (res.code === 200) {
            uni.hideLoading();
            uni.showModal({
              title: '申请提交成功',
              content: '您的快递员申请已提交成功，我们将尽快审核，请留意短信或APP通知。',
              showCancel: false,
              success: () => {
                uni.navigateBack();
              }
            });
          } else {
            throw new Error(res.message || '申请提交失败');
          }
        })
        .catch(err => {
          console.error('提交申请失败', err);
          uni.hideLoading();
          uni.showToast({
            title: err.message || '提交失败，请重试',
            icon: 'none'
          });
        })
        .finally(() => {
          this.submitting = false;
        });
    }
  }
};
</script>

<style>
.recruitment-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.header {
  background-color: #3cc51f;
  color: #fff;
  padding: 40rpx 30rpx;
  text-align: center;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.subtitle {
  font-size: 28rpx;
  opacity: 0.8;
}

.section {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 10rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 30rpx;
  position: relative;
  padding-left: 20rpx;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 6rpx;
  width: 6rpx;
  height: 30rpx;
  background-color: #3cc51f;
  border-radius: 3rpx;
}

/* 收益模块 */
.income-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.income-item {
  display: flex;
  align-items: center;
}

.income-icon {
  width: 80rpx;
  height: 80rpx;
  background-color: rgba(60, 197, 31, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  margin-right: 20rpx;
}

.income-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 4rpx;
}

.income-desc {
  font-size: 24rpx;
  color: #666;
}

/* 申请条件 */
.requirement-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.requirement-item {
  display: flex;
  align-items: center;
}

.requirement-dot {
  width: 12rpx;
  height: 12rpx;
  background-color: #3cc51f;
  border-radius: 50%;
  margin-right: 16rpx;
}

.requirement-text {
  font-size: 28rpx;
  color: #333;
}

/* 申请流程 */
.process-list {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.process-item {
  display: flex;
  align-items: center;
}

.process-step {
  width: 60rpx;
  height: 60rpx;
  background-color: #3cc51f;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: bold;
  margin-right: 20rpx;
}

.process-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 4rpx;
}

.process-desc {
  font-size: 24rpx;
  color: #666;
}

/* 表单 */
.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.form-input {
  width: 100%;
  height: 80rpx;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
}

.form-picker {
  width: 100%;
  height: 80rpx;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
  display: flex;
  align-items: center;
}

.picker-text, .picker-placeholder {
  font-size: 28rpx;
}

.picker-placeholder {
  color: #999;
}

/* 身份证上传 */
.id-card-section {
  margin-bottom: 30rpx;
}

.id-card-upload {
  display: flex;
  justify-content: space-between;
  margin-top: 10rpx;
}

.id-card-item {
  width: 48%;
  height: 180rpx;
  border: 1rpx dashed #ddd;
  border-radius: 8rpx;
  overflow: hidden;
}

.id-card-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 24rpx;
}

.id-card-image {
  width: 100%;
  height: 100%;
}

/* 协议 */
.agreement {
  margin-bottom: 30rpx;
}

.agreement-label {
  display: flex;
  align-items: center;
}

.agreement-text {
  font-size: 26rpx;
  color: #666;
  margin-left: 10rpx;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 88rpx;
  background-color: #3cc51f;
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn[disabled] {
  background-color: #a9e99f;
}

/* 联系方式 */
.contact-section {
  text-align: center;
  padding: 30rpx;
  color: #999;
}

.contact-title {
  font-size: 28rpx;
  margin-bottom: 10rpx;
}

.contact-info {
  font-size: 24rpx;
  line-height: 1.8;
}
</style> 