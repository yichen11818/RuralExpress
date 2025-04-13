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
import { 
  payOrder, 
  getPaymentStatus, 
  cancelPayment, 
  getUserWallet, 
  getPaymentChannels,
  mockPaymentSuccess 
} from '@/api/payment.js';
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
      paymentId: null, // 添加支付ID字段用于模拟支付
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
        this.orderData = data.order || {};
        if (!this.amount || this.amount === '0.00') {
          this.amount = this.orderData.totalFee.toFixed(2);
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
    async handlePayment() {
      if (this.processing) return;
      
      this.processing = true;
      uni.showLoading({
        title: '处理支付请求...'
      });
      
      // 检查订单ID
      if (!this.orderId && this.orderData && this.orderData.id) {
        this.orderId = this.orderData.id;
      }
      
      if (!this.orderId) {
        uni.hideLoading();
        uni.showToast({
          title: '订单ID不存在',
          icon: 'none'
        });
        this.processing = false;
        return;
      }
      
      // 构建支付参数
      const paymentParams = {
        orderId: this.orderId,
        paymentMethod: this.selectedMethod,
        amount: parseFloat(this.amount)
      };
      
      console.log('支付参数:', paymentParams);
      
      try {
        // 调用支付API
        const resp = await payOrder(paymentParams);
        console.log("支付参数返回:", resp);
        
        if (resp.code === 200 && resp.data) {
          // 保存支付记录ID，用于后续查询和模拟支付
          this.paymentId = resp.data.paymentId;
          uni.hideLoading();
          
          // 根据支付方式处理
          if (this.selectedMethod === 'wxpay') {
            // 检查是否是开发环境
            if (process.env.NODE_ENV === 'development') {
              // 开发环境直接使用模拟支付
              console.log('开发环境，直接使用模拟支付');
              uni.showLoading({ title: '模拟支付中...' });
              try {
                await mockPaymentSuccess(this.orderId);
                uni.hideLoading();
                this.handlePaymentSuccess();
              } catch (e) {
                uni.hideLoading();
                uni.showToast({
                  title: '模拟支付失败',
                  icon: 'none'
                });
                this.processing = false;
              }
            } else {
              // 生产环境才调用真实微信支付
              try {
                console.log('调用微信支付参数:', resp.data);
                await this.callWxPay(resp.data);
                this.handlePaymentSuccess();
              } catch (error) {
                console.error('微信支付失败', error);
                this.handlePaymentFail();
              }
            }
          } else if (this.selectedMethod === 'alipay') {
            if (process.env.NODE_ENV === 'development') {
              // 开发环境直接使用模拟支付，不显示弹窗
              console.log('开发环境，直接模拟支付宝支付成功');
              uni.showLoading({ title: '模拟支付中...' });
              try {
                await mockPaymentSuccess(this.orderId);
                uni.hideLoading();
                this.handlePaymentSuccess();
              } catch (e) {
                uni.hideLoading();
                uni.showToast({
                  title: '模拟支付失败',
                  icon: 'none'
                });
                this.processing = false;
              }
            } else {
              // 生产环境调用支付宝支付
              uni.requestPayment({
                provider: 'alipay',
                ...resp.data,
                success: () => {
                  this.handlePaymentSuccess();
                },
                fail: (err) => {
                  console.error('支付宝支付失败', err);
                  this.handlePaymentFail();
                }
              });
            }
          } else if (this.selectedMethod === 'balance') {
            // 余额支付，也使用模拟支付
            uni.showLoading({ title: '余额支付中...' });
            try {
              await mockPaymentSuccess(this.orderId);
              uni.hideLoading();
              this.handlePaymentSuccess();
            } catch (e) {
              uni.hideLoading();
              uni.showToast({
                title: '支付失败',
                icon: 'none'
              });
              this.processing = false;
            }
          }
        } else {
          throw new Error(resp.message || '支付申请失败');
        }
      } catch (error) {
        console.error('支付失败', error);
        if (uni.hideLoading) {
          uni.hideLoading();
        }
        uni.showToast({
          title: error.message || '支付申请失败',
          icon: 'none'
        });
        this.processing = false;
      }
    },
    
    // 调用微信支付的方法
    callWxPay(payParams) {
      return new Promise((resolve, reject) => {
        uni.requestPayment({
          provider: 'wxpay',
          ...payParams,
          success: (res) => {
            console.log('微信支付成功', res);
            resolve(res);
          },
          fail: (err) => {
            console.error('微信支付失败', err);
            reject(err);
          }
        });
      });
    },
    
    // 处理支付成功
    handlePaymentSuccess() {
      uni.showToast({
        title: '支付成功',
        icon: 'success'
      });
      
      setTimeout(() => {
        // 确保使用有效的订单ID
        const orderId = this.orderId || (this.orderData ? this.orderData.id : null);
        if (!orderId) {
          console.error('跳转失败：订单ID不存在');
          uni.navigateBack();
          return;
        }
        
        // 查看有哪些页面可以跳转
        const pages = getCurrentPages();
        console.log('当前页面栈:', pages);
        
        // 尝试跳转到订单列表页面
        try {
          uni.redirectTo({
            url: '/pages/order/index',
            fail: (err) => {
              console.error('跳转到订单列表失败:', err);
              // 如果跳转失败，尝试返回上一页
              uni.navigateBack({
                delta: 1,
                fail: () => {
                  // 如果返回上一页也失败，跳转到首页
                  uni.switchTab({
                    url: '/pages/index/index'
                  });
                }
              });
            }
          });
        } catch (error) {
          console.error('跳转错误:', error);
          // 如果发生错误，尝试返回上一页
          uni.navigateBack();
        }
      }, 1500);
    },
    
    // 处理支付失败
    handlePaymentFail() {
      if (uni.hideLoading) {
        uni.hideLoading();
      }
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
  height: 100rpx;
  padding-top: 80rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.back-btn {
  position: absolute;
  left: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60rpx;
  height: 60rpx;
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