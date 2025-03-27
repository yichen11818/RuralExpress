<template>
  <view class="withdraw-container">
    <!-- 头部信息 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="title">余额提现</text>
    </view>
    
    <!-- 可提现余额 -->
    <view class="balance-info">
      <text class="balance-label">可提现余额</text>
      <view class="balance-value">
        <text class="currency">¥</text>
        <text class="amount">{{ availableBalance }}</text>
      </view>
    </view>
    
    <!-- 提现金额 -->
    <view class="amount-section">
      <view class="section-title">提现金额</view>
      <view class="amount-input">
        <text class="currency">¥</text>
        <input 
          type="digit" 
          v-model="amount" 
          class="input" 
          placeholder="请输入提现金额"
          focus
          @input="validateAmount"
        />
      </view>
      <view class="withdraw-all" @click="withdrawAll">全部提现</view>
    </view>
    
    <!-- 收款方式 -->
    <view class="withdraw-methods">
      <view class="section-title">提现方式</view>
      <radio-group @change="handleMethodChange">
        <view 
          class="method-item" 
          v-for="(item, index) in withdrawMethods" 
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
    
    <!-- 收款账户 -->
    <view class="account-section" v-if="selectedMethod">
      <view class="section-title">收款账户</view>
      
      <!-- 微信账户 -->
      <view v-if="selectedMethod === 'wxpay'">
        <view class="account-info wxpay">
          <image src="/static/images/wxpay.png" mode="aspectFit" class="account-icon"></image>
          <view class="account-detail">
            <text class="account-name">微信账户</text>
            <text class="account-hint">提现将转入绑定的微信账户</text>
          </view>
        </view>
      </view>
      
      <!-- 支付宝账户 -->
      <view v-if="selectedMethod === 'alipay'">
        <view class="account-input">
          <input 
            type="text" 
            v-model="alipayAccount" 
            placeholder="请输入支付宝账号" 
            class="input"
          />
        </view>
        <view class="account-input" v-if="alipayAccount">
          <input 
            type="text" 
            v-model="alipayName" 
            placeholder="请输入支付宝实名" 
            class="input"
          />
        </view>
      </view>
      
      <!-- 银行卡 -->
      <view v-if="selectedMethod === 'bank'">
        <view class="bank-selector" @click="showBankSelector = true" v-if="!selectedBank">
          <text class="bank-placeholder">请选择银行卡</text>
          <uni-icons type="right" size="16" color="#999"></uni-icons>
        </view>
        
        <view class="bank-card" v-else>
          <view class="bank-info">
            <image :src="selectedBank.icon || '/static/images/bank.png'" mode="aspectFit" class="bank-icon"></image>
            <view class="bank-detail">
              <text class="bank-name">{{ selectedBank.name }}</text>
              <text class="card-number">{{ maskCardNumber(selectedBank.cardNumber) }}</text>
            </view>
          </view>
          <text class="change-btn" @click="showBankSelector = true">更换</text>
        </view>
        
        <!-- 添加银行卡 -->
        <view class="add-bank" v-if="!hasBankCard" @click="navigateTo('/pages/wallet/add-bank')">
          <uni-icons type="plus" size="16" color="#3cc51f"></uni-icons>
          <text class="add-text">添加银行卡</text>
        </view>
      </view>
    </view>
    
    <!-- 提现说明 -->
    <view class="withdraw-notice">
      <view class="notice-title">
        <uni-icons type="info" size="16" color="#666"></uni-icons>
        <text>提现说明</text>
      </view>
      <view class="notice-content">
        <text>1. 提现金额将在1-3个工作日内到账</text>
        <text>2. 单笔提现金额不低于10元</text>
        <text>3. 提现手续费为金额的0.5%，最低0.1元</text>
        <text>4. 如有疑问，请联系客服</text>
      </view>
    </view>
    
    <!-- 提现按钮 -->
    <view class="footer">
      <view class="fee-info" v-if="isValidAmount">
        <text>手续费：¥{{ withdrawFee.toFixed(2) }}</text>
        <text>实际到账：¥{{ actualAmount.toFixed(2) }}</text>
      </view>
      <button 
        class="withdraw-btn" 
        type="primary" 
        @click="handleWithdraw"
        :disabled="!canWithdraw || processing"
      >
        {{ processing ? '处理中...' : '确认提现' }}
      </button>
    </view>
    
    <!-- 银行卡选择弹窗 -->
    <uni-popup ref="bankSelector" type="bottom" @change="onPopupChange">
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">选择银行卡</text>
          <text class="popup-close" @click="showBankSelector = false">关闭</text>
        </view>
        <view class="bank-list">
          <view 
            class="bank-item" 
            v-for="(item, index) in bankCards" 
            :key="index"
            @click="selectBank(item)"
          >
            <view class="bank-info">
              <image :src="item.icon || '/static/images/bank.png'" mode="aspectFit" class="bank-icon"></image>
              <view class="bank-detail">
                <text class="bank-name">{{ item.name }}</text>
                <text class="card-number">{{ maskCardNumber(item.cardNumber) }}</text>
              </view>
            </view>
            <uni-icons type="checkmarkempty" size="20" color="#3cc51f" v-if="selectedBank && selectedBank.id === item.id"></uni-icons>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue';
import uniPopup from '@/uni_modules/uni-popup/components/uni-popup/uni-popup.vue';
import { isLoggedIn } from '@/api/auth';
import { getUserWallet } from '@/api/payment';

export default {
  components: {
    uniIcons,
    uniPopup
  },
  data() {
    return {
      availableBalance: '0.00',
      amount: '',
      isValidAmount: false,
      selectedMethod: 'wxpay',
      processing: false,
      withdrawMethods: [
        { 
          name: '微信提现', 
          value: 'wxpay', 
          icon: '/static/images/wxpay.png'
        },
        { 
          name: '支付宝提现', 
          value: 'alipay', 
          icon: '/static/images/alipay.png'
        },
        { 
          name: '银行卡提现', 
          value: 'bank', 
          icon: '/static/images/bank.png'
        }
      ],
      alipayAccount: '',
      alipayName: '',
      showBankSelector: false,
      selectedBank: null,
      bankCards: [
        {
          id: 1,
          name: '中国工商银行',
          cardNumber: '6222021234567890123',
          icon: '/static/images/icbc.png'
        },
        {
          id: 2,
          name: '中国建设银行',
          cardNumber: '6227002345678901234',
          icon: '/static/images/ccb.png'
        }
      ],
      withdrawFee: 0,
      actualAmount: 0
    };
  },
  
  computed: {
    // 是否有银行卡
    hasBankCard() {
      return this.bankCards && this.bankCards.length > 0;
    },
    
    // 是否可以提现
    canWithdraw() {
      if (!this.isValidAmount) return false;
      
      // 检查提现方式和相关信息
      if (this.selectedMethod === 'alipay') {
        return !!this.alipayAccount && !!this.alipayName;
      } else if (this.selectedMethod === 'bank') {
        return !!this.selectedBank;
      }
      
      return true;
    }
  },
  
  watch: {
    // 监听银行卡选择器状态
    showBankSelector(newVal) {
      if (newVal) {
        this.$refs.bankSelector.open();
      } else {
        this.$refs.bankSelector.close();
      }
    }
  },
  
  onLoad() {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 获取钱包余额
    this.loadWalletBalance();
  },
  
  methods: {
    // 加载钱包余额
    loadWalletBalance() {
      uni.showLoading({
        title: '加载中...'
      });
      
      getUserWallet()
        .then(res => {
          if (res.code === 200 && res.data) {
            this.availableBalance = res.data.balance || '0.00';
          } else {
            uni.showToast({
              title: res.message || '获取余额失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取余额失败', err);
          uni.showToast({
            title: '获取余额失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
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
      const numBalance = parseFloat(this.availableBalance);
      
      this.isValidAmount = !isNaN(numAmount) && numAmount >= 10 && numAmount <= numBalance;
      
      // 计算手续费和实际到账金额
      if (this.isValidAmount) {
        this.withdrawFee = Math.max(numAmount * 0.005, 0.1);
        this.actualAmount = numAmount - this.withdrawFee;
      } else {
        this.withdrawFee = 0;
        this.actualAmount = 0;
      }
    },
    
    // 全部提现
    withdrawAll() {
      this.amount = this.availableBalance;
      this.validateAmount();
    },
    
    // 选择提现方式
    handleMethodChange(e) {
      this.selectedMethod = e.detail.value;
    },
    
    // 选择银行卡
    selectBank(bank) {
      this.selectedBank = bank;
      this.showBankSelector = false;
    },
    
    // 弹窗状态变化
    onPopupChange(e) {
      if (!e.show) {
        this.showBankSelector = false;
      }
    },
    
    // 掩码银行卡号
    maskCardNumber(cardNumber) {
      if (!cardNumber) return '';
      const len = cardNumber.length;
      return cardNumber.substring(0, 4) + ' **** **** ' + cardNumber.substring(len - 4);
    },
    
    // 处理提现
    handleWithdraw() {
      if (!this.canWithdraw || this.processing) return;
      
      const numAmount = parseFloat(this.amount);
      const numBalance = parseFloat(this.availableBalance);
      
      // 再次检查金额
      if (numAmount < 10) {
        uni.showToast({
          title: '提现金额不能低于10元',
          icon: 'none'
        });
        return;
      }
      
      if (numAmount > numBalance) {
        uni.showToast({
          title: '余额不足',
          icon: 'none'
        });
        return;
      }
      
      // 准备提现信息
      const withdrawInfo = {
        amount: numAmount,
        actualAmount: this.actualAmount,
        fee: this.withdrawFee,
        method: this.selectedMethod
      };
      
      // 添加不同方式的特定信息
      if (this.selectedMethod === 'alipay') {
        if (!this.alipayAccount || !this.alipayName) {
          uni.showToast({
            title: '请完善支付宝账户信息',
            icon: 'none'
          });
          return;
        }
        withdrawInfo.alipayAccount = this.alipayAccount;
        withdrawInfo.alipayName = this.alipayName;
      } else if (this.selectedMethod === 'bank') {
        if (!this.selectedBank) {
          uni.showToast({
            title: '请选择银行卡',
            icon: 'none'
          });
          return;
        }
        withdrawInfo.bankId = this.selectedBank.id;
      }
      
      this.processing = true;
      
      uni.showLoading({
        title: '处理中...',
        mask: true
      });
      
      // 模拟提现过程
      setTimeout(() => {
        uni.hideLoading();
        
        uni.showModal({
          title: '提现申请已提交',
          content: `提现金额：¥${numAmount.toFixed(2)}\n实际到账：¥${this.actualAmount.toFixed(2)}\n预计1-3个工作日内到账`,
          showCancel: false,
          success: () => {
            // 返回钱包页面
            uni.navigateBack();
          }
        });
      }, 1500);
      
      /*
      // 调用提现API
      withdraw(withdrawInfo)
        .then(res => {
          if (res.code === 200) {
            uni.hideLoading();
            uni.showModal({
              title: '提现申请已提交',
              content: `提现金额：¥${numAmount.toFixed(2)}\n实际到账：¥${this.actualAmount.toFixed(2)}\n预计1-3个工作日内到账`,
              showCancel: false,
              success: () => {
                // 返回钱包页面
                uni.navigateBack();
              }
            });
          } else {
            throw new Error(res.message || '提现申请失败');
          }
        })
        .catch(err => {
          console.error('提现失败', err);
          uni.hideLoading();
          uni.showToast({
            title: err.message || '提现申请失败',
            icon: 'none'
          });
          this.processing = false;
        });
      */
    },
    
    // 导航到指定页面
    navigateTo(url) {
      uni.navigateTo({
        url
      });
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style>
.withdraw-container {
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

/* 余额信息 */
.balance-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 0;
  background-color: #fff;
}

.balance-label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.balance-value {
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 30rpx;
  margin-right: 8rpx;
}

.amount {
  font-size: 50rpx;
  font-weight: bold;
}

/* 金额部分 */
.amount-section {
  background-color: #fff;
  margin: 20rpx 30rpx;
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
}

.input {
  flex: 1;
  height: 80rpx;
  font-size: 40rpx;
  font-weight: bold;
}

.withdraw-all {
  text-align: right;
  font-size: 26rpx;
  color: #3cc51f;
  margin-top: 10rpx;
}

/* 提现方式 */
.withdraw-methods {
  background-color: #fff;
  margin: 0 30rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.method-item:last-child {
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

/* 收款账户 */
.account-section {
  background-color: #fff;
  margin: 0 30rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.account-info {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 8rpx;
}

.account-icon {
  width: 60rpx;
  height: 60rpx;
  margin-right: 20rpx;
}

.account-detail {
  flex: 1;
}

.account-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 6rpx;
}

.account-hint {
  font-size: 24rpx;
  color: #999;
}

.account-input {
  border-bottom: 1rpx solid #e0e0e0;
  margin-bottom: 20rpx;
}

/* 银行卡部分 */
.bank-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 8rpx;
}

.bank-placeholder {
  font-size: 28rpx;
  color: #999;
}

.bank-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 8rpx;
}

.bank-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.bank-icon {
  width: 60rpx;
  height: 60rpx;
  margin-right: 20rpx;
}

.bank-detail {
  flex: 1;
}

.bank-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 6rpx;
}

.card-number {
  font-size: 24rpx;
  color: #666;
}

.change-btn {
  font-size: 26rpx;
  color: #3cc51f;
}

.add-bank {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0;
}

.add-text {
  font-size: 28rpx;
  color: #3cc51f;
  margin-left: 10rpx;
}

/* 提现说明 */
.withdraw-notice {
  margin: 0 30rpx 20rpx;
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

/* 底部操作 */
.footer {
  padding: 0 40rpx;
  margin-top: 60rpx;
}

.fee-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20rpx;
  font-size: 26rpx;
  color: #666;
}

.withdraw-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
}

/* 弹窗样式 */
.popup-content {
  background-color: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding-bottom: 30rpx;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
}

.popup-close {
  font-size: 28rpx;
  color: #666;
}

.bank-list {
  max-height: 600rpx;
  overflow-y: auto;
}

.bank-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.bank-item:last-child {
  border-bottom: none;
}
</style> 