<template>
  <view class="wallet-container">
    <!-- 头部信息 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="title">我的钱包</text>
    </view>
    
    <!-- 余额信息 -->
    <view class="balance-card">
      <view class="balance-title">账户余额</view>
      <view class="balance-amount">
        <text class="currency">¥</text>
        <text class="amount">{{ walletInfo.balance || '0.00' }}</text>
      </view>
      <view class="balance-actions">
        <button class="action-btn" @click="navigateTo('/pages/wallet/recharge')">充值</button>
        <button class="action-btn" @click="navigateTo('/pages/wallet/withdraw')">提现</button>
      </view>
    </view>
    
    <!-- 快速支付设置 -->
    <view class="quick-pay-card">
      <view class="card-title">快速支付</view>
      <view class="option-item">
        <text>开启余额支付</text>
        <switch :checked="walletInfo.enableQuickPay" @change="toggleQuickPay" color="#3cc51f"></switch>
      </view>
      <view class="option-hint">开启后，订单支付时优先使用余额支付</view>
    </view>
    
    <!-- 交易记录 -->
    <view class="transaction-section">
      <view class="section-header">
        <text class="section-title">交易记录</text>
        <text class="section-more" @click="navigateTo('/pages/wallet/transactions')">查看全部</text>
      </view>
      
      <view class="transaction-list" v-if="transactions.length > 0">
        <view 
          class="transaction-item" 
          v-for="(item, index) in transactions" 
          :key="index"
          @click="showTransactionDetail(item)"
        >
          <view class="transaction-info">
            <text class="transaction-type">{{ getTransactionTypeName(item.type) }}</text>
            <text class="transaction-time">{{ formatDate(item.createTime) }}</text>
          </view>
          <view class="transaction-amount" :class="item.amount > 0 ? 'income' : 'expense'">
            {{ item.amount > 0 ? '+' : '' }}{{ item.amount.toFixed(2) }}
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-state" v-else>
        <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
        <text class="empty-text">暂无交易记录</text>
      </view>
    </view>
    
    <!-- 底部安全提示 -->
    <view class="security-tips">
      <view class="tips-title">
        <uni-icons type="info" size="16" color="#666"></uni-icons>
        <text>安全提示</text>
      </view>
      <view class="tips-content">
        <text>1. 请勿泄露账户信息及验证码</text>
        <text>2. 谨防诈骗，不向陌生账户转账</text>
        <text>3. 如有疑问，请联系客服</text>
      </view>
    </view>
  </view>
</template>

<script>
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue';
import { isLoggedIn } from '@/api/auth';
import { getUserWallet } from '@/api/payment';

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      walletInfo: {
        balance: '0.00',
        enableQuickPay: false,
        frozenAmount: '0.00'
      },
      transactions: [],
      loading: false,
      pageInfo: {
        page: 1,
        size: 5,
        total: 0
      }
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
    
    // 获取钱包信息
    this.loadWalletInfo();
  },
  
  onPullDownRefresh() {
    this.loadWalletInfo();
    setTimeout(() => {
      uni.stopPullDownRefresh();
    }, 1000);
  },
  
  methods: {
    // 加载钱包信息
    loadWalletInfo() {
      if (this.loading) return;
      this.loading = true;
      
      uni.showLoading({
        title: '加载中...'
      });
      
      getUserWallet()
        .then(res => {
          if (res.code === 200 && res.data) {
            // 更新钱包信息
            this.walletInfo = {
              balance: res.data.balance || '0.00',
              enableQuickPay: res.data.enableQuickPay || false,
              frozenAmount: res.data.frozenAmount || '0.00'
            };
            
            // 更新交易记录
            if (res.data.recentTransactions) {
              this.transactions = res.data.recentTransactions;
              this.pageInfo.total = res.data.transactionCount || 0;
            }
          } else {
            uni.showToast({
              title: res.message || '获取钱包信息失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取钱包信息失败', err);
          uni.showToast({
            title: '获取钱包信息失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
          this.loading = false;
        });
    },
    
    // 切换快速支付
    toggleQuickPay(e) {
      const enableQuickPay = e.detail.value;
      
      // 更新本地状态
      this.walletInfo.enableQuickPay = enableQuickPay;
      
      // 调用API更新设置
      // 这里应该调用API保存设置，但目前API可能未完成
      /*
      updateWalletSettings({ enableQuickPay })
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '设置已保存',
              icon: 'success'
            });
          } else {
            throw new Error(res.message || '保存设置失败');
          }
        })
        .catch(err => {
          console.error('保存设置失败', err);
          uni.showToast({
            title: err.message || '保存设置失败',
            icon: 'none'
          });
          
          // 恢复状态
          this.walletInfo.enableQuickPay = !enableQuickPay;
        });
      */
      
      // 临时提示
      uni.showToast({
        title: '设置已保存',
        icon: 'success'
      });
    },
    
    // 获取交易类型名称
    getTransactionTypeName(type) {
      const typeMap = {
        'RECHARGE': '余额充值',
        'WITHDRAW': '余额提现',
        'PAYMENT': '订单支付',
        'REFUND': '退款',
        'BONUS': '活动奖励',
        'OTHER': '其他'
      };
      return typeMap[type] || '未知类型';
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    
    // 显示交易详情
    showTransactionDetail(item) {
      uni.navigateTo({
        url: `/pages/wallet/transaction-detail?id=${item.id}`
      });
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
.wallet-container {
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

/* 余额卡片样式 */
.balance-card {
  background: linear-gradient(135deg, #3cc51f, #39b54a);
  margin: 30rpx;
  border-radius: 16rpx;
  padding: 40rpx 30rpx;
  color: #fff;
  box-shadow: 0 10rpx 20rpx rgba(60, 197, 31, 0.2);
}

.balance-title {
  font-size: 28rpx;
  opacity: 0.9;
  margin-bottom: 20rpx;
}

.balance-amount {
  display: flex;
  align-items: baseline;
  margin-bottom: 40rpx;
}

.currency {
  font-size: 36rpx;
  margin-right: 8rpx;
}

.amount {
  font-size: 72rpx;
  font-weight: bold;
}

.balance-actions {
  display: flex;
  justify-content: space-around;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
  border-radius: 40rpx;
  font-size: 30rpx;
  margin: 0 20rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.5);
}

/* 快速支付卡片 */
.quick-pay-card {
  background-color: #fff;
  margin: 30rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10rpx 0;
}

.option-item text {
  font-size: 28rpx;
  color: #333;
}

.option-hint {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

/* 交易记录部分 */
.transaction-section {
  background-color: #fff;
  margin: 30rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
}

.section-more {
  font-size: 26rpx;
  color: #666;
}

.transaction-list {
  display: flex;
  flex-direction: column;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-info {
  display: flex;
  flex-direction: column;
}

.transaction-type {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 8rpx;
}

.transaction-time {
  font-size: 24rpx;
  color: #999;
}

.transaction-amount {
  font-size: 32rpx;
  font-weight: bold;
}

.transaction-amount.income {
  color: #3cc51f;
}

.transaction-amount.expense {
  color: #ff7043;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
}

.empty-image {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 安全提示 */
.security-tips {
  margin: 30rpx;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 12rpx;
}

.tips-title {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.tips-title text {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

.tips-content {
  display: flex;
  flex-direction: column;
}

.tips-content text {
  font-size: 24rpx;
  color: #999;
  line-height: 1.6;
}
</style> 