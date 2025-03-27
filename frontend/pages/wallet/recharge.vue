<template>
  <view class="recharge-container">
    <!-- 头部信息 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="title">余额充值</text>
    </view>
    
    <!-- 充值金额 -->
    <view class="amount-section">
      <view class="section-title">充值金额</view>
      <view class="amount-input">
        <text class="currency">¥</text>
        <input 
          type="digit" 
          v-model="amount" 
          class="input" 
          placeholder="请输入充值金额"
          focus
          @input="validateAmount"
        />
      </view>
      <view class="amount-options">
        <view 
          class="amount-option" 
          v-for="(item, index) in amountOptions" 
          :key="index"
          :class="{ active: amount === item.value }"
          @click="selectAmount(item.value)"
        >
          {{ item.label }}
        </view>
      </view>
    </view>
    
    <!-- 支付方式 -->
    <view class="payment-methods">
      <view class="section-title">充值方式</view>
      <radio-group @change="handlePaymentChange">
        <view 
          class="payment-method-item" 
          v-for="(item, index) in paymentMethods" 
          :key="index"
          @click="selectedMethod = item.value"
        >
          <view class="method-info">
            <image :src="item.icon" mode="aspectFit" class="method-icon"></image>
            <text class="method-name">{{ item.name }}</text>
          </view>
          <radio :value="item.value" :checked="selectedMethod === item.value" color="#3cc51f" />
        </view>
      </radio-group>
    </view>
    
    <!-- 充值说明 -->
    <view class="recharge-notice">
      <view class="notice-title">
        <uni-icons type="info" size="16" color="#666"></uni-icons>
        <text>充值说明</text>
      </view>
      <view class="notice-content">
        <text>1. 充值金额将实时到账</text>
        <text>2. 如遇到充值问题，请联系客服</text>
        <text>3. 余额仅用于平台内消费，不支持提现</text>
      </view>
    </view>
    
    <!-- 充值按钮 -->
    <view class="footer">
      <button 
        class="recharge-btn" 
        type="primary" 
        @click="handleRecharge"
        :disabled="!isValidAmount || processing"
      >
        {{ processing ? '处理中...' : '立即充值' }}
      </button>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue';
import { isLoggedIn } from '@/api/auth';

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      amount: '',
      isValidAmount: false,
      selectedMethod: 'wxpay',
      processing: false,
      amountOptions: [
        { label: '¥10', value: '10' },
        { label: '¥20', value: '20' },
        { label: '¥50', value: '50' },
        { label: '¥100', value: '100' },
        { label: '¥200', value: '200' },
        { label: '¥500', value: '500' }
      ],
      paymentMethods: [
        { 
          name: '微信支付', 
          value: 'wxpay', 
          icon: '/static/images/wxpay.png'
        },
        { 
          name: '支付宝', 
          value: 'alipay', 
          icon: '/static/images/alipay.png'
        }
      ]
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
  },
  
  methods: {
    // 验证金额
    validateAmount() {
      // 去除非数字和小数点
      this.amount = this.amount.replace(/[^\d.]/g, '');
      
      // 检查是否有多个小数点
      const dotIndex = this.amount.indexOf('.');
      if (dotIndex !== -1) {
        const secDotIndex = this.amount.indexOf('.', dotIndex + 1);
        if (secDotIndex !== -1) {
          this.amount = this.amount.substring(0, secDotIndex);
        }
        
        // 限制小数位
        if (this.amount.length > dotIndex + 3) {
          this.amount = this.amount.substring(0, dotIndex + 3);
        }
      }
      
      // 检查是否是有效金额
      const numAmount = parseFloat(this.amount);
      this.isValidAmount = !isNaN(numAmount) && numAmount >= 0.01 && numAmount <= 50000;
    },
    
    // 选择金额
    selectAmount(value) {
      this.amount = value;
      this.validateAmount();
    },
    
    // 选择支付方式
    handlePaymentChange(e) {
      this.selectedMethod = e.detail.value;
    },
    
    // 处理充值
    handleRecharge() {
      if (!this.isValidAmount || this.processing) return;
      
      this.processing = true;
      
      uni.showLoading({
        title: '正在处理...',
        mask: true
      });
      
      // 这里应该调用充值API，但目前API可能未完成
      // 模拟充值过程
      setTimeout(() => {
        uni.hideLoading();
        
        // 根据支付方式调用不同的支付
        if (this.selectedMethod === 'wxpay') {
          // 模拟调用微信支付
          uni.showModal({
            title: '提示',
            content: '即将调用微信支付',
            confirmText: '确认支付',
            success: (res) => {
              if (res.confirm) {
                this.handleRechargeSuccess();
              } else {
                this.handleRechargeFail();
              }
            }
          });
        } else if (this.selectedMethod === 'alipay') {
          // 模拟调用支付宝支付
          uni.showModal({
            title: '提示',
            content: '即将调用支付宝支付',
            confirmText: '确认支付',
            success: (res) => {
              if (res.confirm) {
                this.handleRechargeSuccess();
              } else {
                this.handleRechargeFail();
              }
            }
          });
        }
      }, 1000);
    },
    
    // 处理充值成功
    handleRechargeSuccess() {
      uni.showToast({
        title: '充值成功',
        icon: 'success'
      });
      
      setTimeout(() => {
        // 返回钱包页面
        uni.navigateBack();
      }, 1500);
    },
    
    // 处理充值失败
    handleRechargeFail() {
      uni.showToast({
        title: '充值已取消',
        icon: 'none'
      });
      this.processing = false;
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style>
.recharge-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 90rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.back-btn {
  position: absolute;
  left: 30rpx;
}

.title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
}

/* 金额部分 */
.amount-section {
  background-color: #fff;
  margin: 30rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.amount-input {
  display: flex;
  align-items: center;
  border-bottom: 1rpx solid #e0e0e0;
  padding-bottom: 20rpx;
  margin-bottom: 30rpx;
}

.currency {
  font-size: 40rpx;
  font-weight: bold;
  margin-right: 20rpx;
}

.input {
  flex: 1;
  height: 80rpx;
  font-size: 40rpx;
  font-weight: bold;
}

.amount-options {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.amount-option {
  width: 30%;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background-color: #f5f5f5;
  color: #333;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
  font-size: 30rpx;
}

.amount-option.active {
  background-color: #e6f7e8;
  color: #3cc51f;
  border: 1rpx solid #3cc51f;
}

/* 支付方式 */
.payment-methods {
  background-color: #fff;
  margin: 0 30rpx 30rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.payment-method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.payment-method-item:last-child {
  border-bottom: none;
}

.method-info {
  display: flex;
  align-items: center;
}

.method-icon {
  width: 60rpx;
  height: 60rpx;
  margin-right: 20rpx;
}

.method-name {
  font-size: 30rpx;
  color: #333;
}

/* 充值说明 */
.recharge-notice {
  margin: 0 30rpx 30rpx;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 12rpx;
}

.notice-title {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.notice-title text {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

.notice-content {
  display: flex;
  flex-direction: column;
}

.notice-content text {
  font-size: 24rpx;
  color: #999;
  line-height: 1.6;
}

/* 充值按钮 */
.footer {
  padding: 0 40rpx;
  margin-top: 60rpx;
}

.recharge-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
}
</style> 