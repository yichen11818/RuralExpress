<template>
  <view class="detail-container">
    <!-- 头部信息 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="title">交易详情</text>
    </view>
    
    <!-- 交易金额 -->
    <view class="amount-section" :class="transaction.amount > 0 ? 'income-bg' : 'expense-bg'">
      <view class="amount-type">{{ getTransactionTypeName(transaction.type) }}</view>
      <view class="amount-value">
        <text>{{ transaction.amount > 0 ? '+' : '' }}{{ transaction.amount.toFixed(2) }}</text>
      </view>
      <view class="amount-status">{{ getStatusText(transaction.status) }}</view>
    </view>
    
    <!-- 交易详情 -->
    <view class="detail-section">
      <view class="detail-item">
        <text class="item-label">交易类型</text>
        <text class="item-value">{{ getTransactionTypeName(transaction.type) }}</text>
      </view>
      
      <view class="detail-item" v-if="transaction.orderNo">
        <text class="item-label">关联订单</text>
        <text class="item-value">{{ transaction.orderNo }}</text>
      </view>
      
      <view class="detail-item">
        <text class="item-label">交易时间</text>
        <text class="item-value">{{ formatDate(transaction.createTime) }}</text>
      </view>
      
      <view class="detail-item">
        <text class="item-label">交易单号</text>
        <text class="item-value">{{ transaction.tradeNo || '-' }}</text>
      </view>
      
      <view class="detail-item" v-if="transaction.paymentMethod">
        <text class="item-label">支付方式</text>
        <text class="item-value">{{ getPaymentMethodName(transaction.paymentMethod) }}</text>
      </view>
      
      <view class="detail-item">
        <text class="item-label">交易状态</text>
        <text class="item-value" :class="getStatusClass(transaction.status)">
          {{ getStatusText(transaction.status) }}
        </text>
      </view>
      
      <view class="detail-item" v-if="transaction.remark">
        <text class="item-label">备注</text>
        <text class="item-value">{{ transaction.remark }}</text>
      </view>
    </view>
    
    <!-- 底部操作 -->
    <view class="footer-section" v-if="showActions">
      <button class="action-btn copy-btn" @click="copyTradeNo">
        复制交易号
      </button>
      <button class="action-btn detail-btn" v-if="isOrderRelated" @click="viewOrderDetail">
        查看订单
      </button>
    </view>
    
    <!-- 客服提示 -->
    <view class="help-section">
      <text class="help-text">如有疑问，请联系客服：400-888-8888</text>
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
      id: null,
      transaction: {
        id: 0,
        type: 'OTHER',
        amount: 0,
        createTime: '',
        status: 'SUCCESS',
        orderNo: '',
        tradeNo: '',
        paymentMethod: '',
        remark: ''
      },
      loading: false
    };
  },
  
  computed: {
    // 是否显示操作按钮
    showActions() {
      return this.transaction.tradeNo || this.isOrderRelated;
    },
    
    // 是否与订单相关的交易
    isOrderRelated() {
      return this.transaction.type === 'PAYMENT' || this.transaction.type === 'REFUND';
    }
  },
  
  onLoad(options) {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 获取交易ID
    if (options.id) {
      this.id = options.id;
      // 加载交易详情
      this.loadTransactionDetail();
    } else {
      uni.showToast({
        title: '缺少交易ID',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },
  
  methods: {
    // 加载交易详情
    loadTransactionDetail() {
      if (this.loading) return;
      this.loading = true;
      
      uni.showLoading({
        title: '加载中...'
      });
      
      // 模拟获取交易详情
      setTimeout(() => {
        // 构造模拟数据
        this.generateMockTransactionDetail();
        
        uni.hideLoading();
        this.loading = false;
      }, 1000);
      
      /*
      // 调用API获取交易详情
      getTransactionDetail(this.id)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.transaction = res.data;
          } else {
            uni.showToast({
              title: res.message || '获取交易详情失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取交易详情失败', err);
          uni.showToast({
            title: '获取交易详情失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
          this.loading = false;
        });
      */
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
    
    // 获取支付方式名称
    getPaymentMethodName(method) {
      const methodMap = {
        'WXPAY': '微信支付',
        'ALIPAY': '支付宝',
        'BALANCE': '余额支付',
        'BANK': '银行卡'
      };
      return methodMap[method] || method;
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'PENDING': '处理中',
        'SUCCESS': '交易成功',
        'FAILED': '交易失败',
        'REFUNDED': '已退款',
        'CANCELED': '已取消'
      };
      return statusMap[status] || status;
    },
    
    // 获取状态样式类
    getStatusClass(status) {
      const classMap = {
        'PENDING': 'status-pending',
        'SUCCESS': 'status-success',
        'FAILED': 'status-failed',
        'REFUNDED': 'status-refunded',
        'CANCELED': 'status-canceled'
      };
      return classMap[status] || '';
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
      const second = String(date.getSeconds()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
    },
    
    // 复制交易号
    copyTradeNo() {
      if (!this.transaction.tradeNo) return;
      
      uni.setClipboardData({
        data: this.transaction.tradeNo,
        success: () => {
          uni.showToast({
            title: '复制成功',
            icon: 'success'
          });
        }
      });
    },
    
    // 查看订单详情
    viewOrderDetail() {
      if (!this.transaction.orderNo) return;
      
      uni.navigateTo({
        url: `/pages/order/detail?id=${this.transaction.orderId}`
      });
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    
    // 生成模拟交易详情
    generateMockTransactionDetail() {
      const types = ['RECHARGE', 'WITHDRAW', 'PAYMENT', 'REFUND', 'BONUS'];
      const paymentMethods = ['WXPAY', 'ALIPAY', 'BALANCE', 'BANK'];
      const statuses = ['SUCCESS', 'PENDING', 'FAILED'];
      
      // 随机选择类型和状态
      const type = types[Math.floor(Math.random() * types.length)];
      const status = statuses[Math.floor(Math.random() * statuses.length)];
      
      // 根据类型决定金额的正负
      let amount = 0;
      if (type === 'RECHARGE' || type === 'REFUND' || type === 'BONUS') {
        amount = Math.floor(Math.random() * 500) + 10; // 正数
      } else {
        amount = -Math.floor(Math.random() * 200) - 10; // 负数
      }
      
      // 生成日期
      const date = new Date();
      date.setHours(date.getHours() - Math.floor(Math.random() * 24));
      
      // 生成交易号
      const tradeNo = `TX${Date.now().toString().substring(5)}${this.id.toString().padStart(4, '0')}`;
      
      // 生成订单号和订单ID（如果是支付或退款）
      let orderNo = '';
      let orderId = '';
      if (type === 'PAYMENT' || type === 'REFUND') {
        orderNo = `ORDER${Date.now().toString().substring(5)}${Math.floor(Math.random() * 1000)}`;
        orderId = Math.floor(Math.random() * 1000) + 1;
      }
      
      // 随机选择支付方式
      const paymentMethod = type === 'RECHARGE' ? 
        paymentMethods[Math.floor(Math.random() * (paymentMethods.length - 1))] :
        (type === 'PAYMENT' ? 'BALANCE' : '');
      
      // 设置交易详情
      this.transaction = {
        id: this.id,
        type: type,
        amount: amount,
        createTime: date.toISOString(),
        status: status,
        orderNo: orderNo,
        orderId: orderId,
        tradeNo: tradeNo,
        paymentMethod: paymentMethod,
        remark: type === 'BONUS' ? '活动奖励' : ''
      };
    }
  }
};
</script>

<style>
.detail-container {
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
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 0;
  color: #fff;
  margin-bottom: 20rpx;
}

.income-bg {
  background: linear-gradient(135deg, #3cc51f, #39b54a);
}

.expense-bg {
  background: linear-gradient(135deg, #ff7043, #ff5722);
}

.amount-type {
  font-size: 30rpx;
  margin-bottom: 10rpx;
}

.amount-value {
  font-size: 60rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.amount-status {
  font-size: 28rpx;
  opacity: 0.8;
}

/* 详情部分 */
.detail-section {
  background-color: #fff;
  margin: 0 30rpx 30rpx;
  border-radius: 12rpx;
  padding: 20rpx 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.detail-item:last-child {
  border-bottom: none;
}

.item-label {
  font-size: 28rpx;
  color: #666;
}

.item-value {
  font-size: 28rpx;
  color: #333;
  word-break: break-all;
}

/* 状态颜色 */
.status-pending {
  color: #ff9900;
}

.status-success {
  color: #3cc51f;
}

.status-failed, .status-canceled {
  color: #ff5722;
}

.status-refunded {
  color: #2196f3;
}

/* 底部操作 */
.footer-section {
  display: flex;
  justify-content: space-between;
  margin: 0 30rpx 30rpx;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  margin: 0 10rpx;
}

.copy-btn {
  background-color: #f5f5f5;
  color: #666;
}

.detail-btn {
  background-color: #3cc51f;
  color: #fff;
}

/* 帮助提示 */
.help-section {
  text-align: center;
  margin: 30rpx 0;
}

.help-text {
  font-size: 24rpx;
  color: #999;
}
</style> 