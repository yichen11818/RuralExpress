<template>
  <view class="payment-container">
    <!-- 顶部信息 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <uni-icons type="left" size="18" color="#333"></uni-icons>
      </view>
      <text class="title">订单支付</text>
    </view>
    
    <!-- 支付金额 -->
    <view class="amount-section">
      <text class="amount-label">支付金额</text>
      <text class="amount-value">¥{{ amount }}</text>
    </view>
    
    <!-- 支付方式 -->
    <view class="payment-methods">
      <view class="section-title">支付方式</view>
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
    
    <!-- 支付按钮 -->
    <view class="footer">
      <button 
        class="pay-btn" 
        type="primary" 
        @click="handlePayment"
        :disabled="processing"
      >
        {{ processing ? '支付处理中...' : '立即支付' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getOrderDetail } from '@/api/order';
import { payOrder } from '@/api/payment';
import uniIcons from '@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue';

export default {
  components: {
    uniIcons
  },
  data() {
    return {
      orderId: null,
      amount: '0.00',
      selectedMethod: 'wxpay', // 默认微信支付
      processing: false,
      orderData: null,
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
        },
        { 
          name: '余额支付', 
          value: 'balance', 
          icon: '/static/images/balance.png'
        }
      ]
    };
  },
  
  onLoad(options) {
    // 获取订单ID和金额
    if (options.orderId) {
      this.orderId = options.orderId;
      
      // 如果有金额参数，直接使用
      if (options.amount) {
        this.amount = options.amount;
      }
      
      // 加载订单详情
      this.loadOrderDetail();
    } else if (options.amount) {
      // 如果只有金额，直接显示
      this.amount = options.amount;
    }
    
    // 获取支付方式
    if (options.method && this.paymentMethods.some(m => m.value === options.method)) {
      this.selectedMethod = options.method;
    }
    
    // 获取订单数据
    const eventChannel = this.getOpenerEventChannel();
    if (eventChannel && eventChannel.on) {
      eventChannel.on('orderData', (data) => {
        this.orderData = data;
        if (!this.amount || this.amount === '0.00') {
          this.amount = data.totalFee.toFixed(2);
        }
      });
    }
  },
  
  methods: {
    // 加载订单详情
    loadOrderDetail() {
      if (!this.orderId) return;
      
      uni.showLoading({
        title: '加载中...'
      });
      
      getOrderDetail(this.orderId)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.orderData = res.data.order || {};
            
            // 如果没有金额，从订单中获取
            if (!this.amount || this.amount === '0.00') {
              this.amount = this.orderData.totalFee.toFixed(2);
            }
          } else {
            uni.showToast({
              title: '获取订单信息失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取订单详情失败', err);
          uni.showToast({
            title: '获取订单信息失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    
    // 选择支付方式
    handlePaymentChange(e) {
      this.selectedMethod = e.detail.value;
    },
    
    // 处理支付
    handlePayment() {
      if (this.processing) return;
      
      if (!this.orderId) {
        uni.showToast({
          title: '订单ID不存在',
          icon: 'none'
        });
        return;
      }
      
      this.processing = true;
      
      uni.showLoading({
        title: '处理支付...',
        mask: true
      });
      
      // 构建支付参数
      const paymentParams = {
        orderId: this.orderId,
        paymentMethod: this.selectedMethod,
        amount: parseFloat(this.amount)
      };
      
      // 调用支付API
      payOrder(paymentParams)
        .then(res => {
          if (res.code === 200) {
            uni.hideLoading();
            
            // 根据不同的支付方式处理
            if (this.selectedMethod === 'wxpay') {
              // 调用微信支付
              uni.requestPayment({
                provider: 'wxpay',
                ...res.data,
                success: () => {
                  this.handlePaymentSuccess();
                },
                fail: (err) => {
                  console.error('微信支付失败', err);
                  this.handlePaymentFail();
                }
              });
            } else if (this.selectedMethod === 'alipay') {
              // 调用支付宝支付
              uni.requestPayment({
                provider: 'alipay',
                ...res.data,
                success: () => {
                  this.handlePaymentSuccess();
                },
                fail: (err) => {
                  console.error('支付宝支付失败', err);
                  this.handlePaymentFail();
                }
              });
            } else if (this.selectedMethod === 'balance') {
              // 余额支付，直接处理成功
              this.handlePaymentSuccess();
            }
          } else {
            throw new Error(res.message || '支付申请失败');
          }
        })
        .catch(err => {
          console.error('支付失败', err);
          uni.hideLoading();
          uni.showToast({
            title: err.message || '支付申请失败',
            icon: 'none'
          });
          this.processing = false;
        });
    },
    
    // 处理支付成功
    handlePaymentSuccess() {
      uni.showToast({
        title: '支付成功',
        icon: 'success'
      });
      
      setTimeout(() => {
        // 跳转到订单详情页
        uni.redirectTo({
          url: `/pages/order/detail?id=${this.orderId}`
        });
      }, 1500);
    },
    
    // 处理支付失败
    handlePaymentFail() {
      uni.showToast({
        title: '支付已取消',
        icon: 'none'
      });
      this.processing = false;
    }
  }
};
</script>

<style>
.payment-container {
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

.amount-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.amount-label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.amount-value {
  font-size: 72rpx;
  font-weight: bold;
  color: #333;
}

.payment-methods {
  background-color: #fff;
  padding: 0 30rpx;
  margin-bottom: 40rpx;
}

.section-title {
  font-size: 30rpx;
  color: #333;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.payment-method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
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

.footer {
  padding: 0 40rpx;
  margin-top: 60rpx;
}

.pay-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background-color: #3cc51f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 45rpx;
}
</style> 