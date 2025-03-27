<template>
  <view class="send-container">
    <view class="form-title">寄件信息</view>
    
    <!-- 寄件人信息 -->
    <view class="form-section">
      <view class="form-header">
        <view class="form-header-text">寄件人信息</view>
        <view class="address-book" @click="showAddressBook('sender')">地址簿</view>
      </view>
      
      <view class="form-content">
        <view class="form-item">
          <text class="form-label">姓名</text>
          <input 
            class="form-input" 
            type="text" 
            v-model="formData.senderName" 
            placeholder="请输入寄件人姓名"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">电话</text>
          <input 
            class="form-input" 
            type="number" 
            v-model="formData.senderPhone" 
            placeholder="请输入寄件人电话"
            maxlength="11"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">地址</text>
          <input 
            class="form-input" 
            type="text" 
            v-model="formData.senderAddress" 
            placeholder="请输入寄件人详细地址"
          />
        </view>
      </view>
    </view>
    
    <!-- 收件人信息 -->
    <view class="form-section">
      <view class="form-header">
        <view class="form-header-text">收件人信息</view>
        <view class="address-book" @click="showAddressBook('receiver')">地址簿</view>
      </view>
      
      <view class="form-content">
        <view class="form-item">
          <text class="form-label">姓名</text>
          <input 
            class="form-input" 
            type="text" 
            v-model="formData.receiverName" 
            placeholder="请输入收件人姓名"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">电话</text>
          <input 
            class="form-input" 
            type="number" 
            v-model="formData.receiverPhone" 
            placeholder="请输入收件人电话"
            maxlength="11"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">地址</text>
          <input 
            class="form-input" 
            type="text" 
            v-model="formData.receiverAddress" 
            placeholder="请输入收件人详细地址"
          />
        </view>
      </view>
    </view>
    
    <!-- 包裹信息 -->
    <view class="form-section">
      <view class="form-header">
        <view class="form-header-text">包裹信息</view>
      </view>
      
      <view class="form-content">
        <view class="form-item">
          <text class="form-label">物品类型</text>
          <picker 
            @change="handlePackageTypeChange" 
            :value="formData.packageType" 
            :range="packageTypes"
          >
            <view class="picker-view">
              <text>{{ packageTypes[formData.packageType] }}</text>
              <uni-icons type="arrowright" size="14" color="#999"></uni-icons>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <text class="form-label">重量(kg)</text>
          <input 
            class="form-input" 
            type="digit" 
            v-model="formData.weight" 
            placeholder="请输入包裹重量"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">保价(元)</text>
          <input 
            class="form-input" 
            type="digit" 
            v-model="formData.insuranceValue" 
            placeholder="选填，最高3000元"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">备注</text>
          <input 
            class="form-input" 
            type="text" 
            v-model="formData.note" 
            placeholder="选填，备注信息"
          />
        </view>
      </view>
    </view>
    
    <!-- 配送服务 -->
    <view class="form-section">
      <view class="form-header">
        <view class="form-header-text">配送服务</view>
      </view>
      
      <view class="form-content">
        <view class="service-item" @click="formData.isUrgent = !formData.isUrgent">
          <view class="service-left">
            <view class="service-title">加急配送</view>
            <view class="service-desc">最快2小时送达，仅限同城</view>
          </view>
          <switch 
            :checked="formData.isUrgent" 
            color="#3cc51f"
            @change="e => formData.isUrgent = e.detail.value"
          />
        </view>
        
        <view class="service-item" @click="formData.needReceipt = !formData.needReceipt">
          <view class="service-left">
            <view class="service-title">签收回执</view>
            <view class="service-desc">配送员将拍照回传签收凭证</view>
          </view>
          <switch 
            :checked="formData.needReceipt" 
            color="#3cc51f"
            @change="e => formData.needReceipt = e.detail.value"
          />
        </view>
      </view>
    </view>
    
    <!-- 费用信息 -->
    <view class="form-section">
      <view class="form-header">
        <view class="form-header-text">费用详情</view>
      </view>
      
      <view class="form-content">
        <view class="fee-item">
          <text>基础运费</text>
          <text>¥{{ calcDeliveryFee().toFixed(2) }}</text>
        </view>
        
        <view class="fee-item" v-if="formData.isUrgent">
          <text>加急服务</text>
          <text>¥{{ urgentFee.toFixed(2) }}</text>
        </view>
        
        <view class="fee-item" v-if="formData.insuranceValue > 0">
          <text>保价费</text>
          <text>¥{{ calcInsuranceFee().toFixed(2) }}</text>
        </view>
        
        <view class="fee-item total-fee">
          <text>合计</text>
          <text class="price">¥{{ calcTotalFee().toFixed(2) }}</text>
        </view>
      </view>
    </view>
    
    <!-- 支付方式 -->
    <view class="form-section">
      <view class="form-header">
        <view class="form-header-text">支付方式</view>
      </view>
      
      <view class="form-content">
        <radio-group @change="handlePaymentChange">
          <label class="payment-item" v-for="(item, index) in paymentMethods" :key="index">
            <view class="payment-left">
              <image class="payment-icon" :src="item.icon" mode="aspectFit"></image>
              <text class="payment-name">{{ item.name }}</text>
            </view>
            <radio :value="item.value" :checked="formData.paymentMethod === item.value" color="#3cc51f" />
          </label>
        </radio-group>
      </view>
    </view>
    
    <!-- 提交按钮 -->
    <view class="submit-section">
      <view class="total-info">
        <text class="total-text">合计：</text>
        <text class="total-price">¥{{ calcTotalFee().toFixed(2) }}</text>
      </view>
      <button class="submit-btn" type="primary" @click="submitOrder">提交订单</button>
    </view>
  </view>
</template>

<script>
import { isLoggedIn, getUserInfo } from '@/api/auth';

export default {
  data() {
    return {
      // 表单数据
      formData: {
        // 寄件人信息
        senderName: '',
        senderPhone: '',
        senderAddress: '',
        
        // 收件人信息
        receiverName: '',
        receiverPhone: '',
        receiverAddress: '',
        
        // 包裹信息
        packageType: 0, // 默认普通快递
        weight: 1.0, // 默认1kg
        insuranceValue: 0, // 默认不保价
        note: '',
        
        // 服务选项
        isUrgent: false, // 是否加急
        needReceipt: false, // 是否需要回执
        
        // 支付方式
        paymentMethod: 'wxpay' // 默认微信支付
      },
      
      // 包裹类型选项
      packageTypes: ['普通快递', '文件', '食品', '电子产品', '易碎品', '其他'],
      
      // 加急费用
      urgentFee: 10.0,
      
      // 支付方式
      paymentMethods: [
        { name: '微信支付', value: 'wxpay', icon: '/static/images/wxpay.png' },
        { name: '支付宝', value: 'alipay', icon: '/static/images/alipay.png' },
        { name: '余额支付', value: 'balance', icon: '/static/images/balance.png' }
      ]
    };
  },
  
  onLoad(options) {
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 获取用户信息
    const userInfo = getUserInfo();
    if (userInfo) {
      // 填充寄件人信息
      this.formData.senderName = userInfo.name || userInfo.nickname || '';
      this.formData.senderPhone = userInfo.phone || '';
    }
    
    // 获取通过事件通道传递的数据（再次下单场景）
    const eventChannel = this.getOpenerEventChannel();
    if (eventChannel && eventChannel.on) {
      eventChannel.on('reorderData', (data) => {
        if (data) {
          this.formData.senderName = data.senderName || this.formData.senderName;
          this.formData.senderPhone = data.senderPhone || this.formData.senderPhone;
          this.formData.senderAddress = data.senderAddress || this.formData.senderAddress;
          this.formData.receiverName = data.receiverName || this.formData.receiverName;
          this.formData.receiverPhone = data.receiverPhone || this.formData.receiverPhone;
          this.formData.receiverAddress = data.receiverAddress || this.formData.receiverAddress;
          this.formData.packageType = data.packageType !== undefined ? data.packageType : this.formData.packageType;
          this.formData.weight = data.weight || this.formData.weight;
        }
      });
    }
  },
  
  methods: {
    // 显示地址簿
    showAddressBook(type) {
      uni.navigateTo({
        url: '/pages/user/address',
        success: (res) => {
          // 监听选择地址事件
          res.eventChannel.once('selectAddress', (data) => {
            if (type === 'sender') {
              this.formData.senderName = data.name;
              this.formData.senderPhone = data.phone;
              this.formData.senderAddress = data.address;
            } else {
              this.formData.receiverName = data.name;
              this.formData.receiverPhone = data.phone;
              this.formData.receiverAddress = data.address;
            }
          });
        }
      });
    },
    
    // 物品类型变更
    handlePackageTypeChange(e) {
      this.formData.packageType = parseInt(e.detail.value);
    },
    
    // 支付方式变更
    handlePaymentChange(e) {
      this.formData.paymentMethod = e.detail.value;
    },
    
    // 计算配送费
    calcDeliveryFee() {
      // 基础运费计算，根据重量梯度计算
      const weight = parseFloat(this.formData.weight) || 0;
      
      if (weight <= 1) {
        return 15;
      } else if (weight <= 5) {
        return 15 + (weight - 1) * 5;
      } else {
        return 35 + (weight - 5) * 4;
      }
    },
    
    // 计算保价费
    calcInsuranceFee() {
      const value = parseFloat(this.formData.insuranceValue) || 0;
      // 保价费率为2%，最低1元
      return Math.max(value * 0.02, value > 0 ? 1 : 0);
    },
    
    // 计算总费用
    calcTotalFee() {
      let total = this.calcDeliveryFee();
      
      // 加急费
      if (this.formData.isUrgent) {
        total += this.urgentFee;
      }
      
      // 保价费
      total += this.calcInsuranceFee();
      
      return total;
    },
    
    // 表单验证
    validateForm() {
      if (!this.formData.senderName) {
        uni.showToast({
          title: '请输入寄件人姓名',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.senderPhone) {
        uni.showToast({
          title: '请输入寄件人电话',
          icon: 'none'
        });
        return false;
      }
      
      if (!/^1\d{10}$/.test(this.formData.senderPhone)) {
        uni.showToast({
          title: '寄件人电话格式不正确',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.senderAddress) {
        uni.showToast({
          title: '请输入寄件人地址',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.receiverName) {
        uni.showToast({
          title: '请输入收件人姓名',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.receiverPhone) {
        uni.showToast({
          title: '请输入收件人电话',
          icon: 'none'
        });
        return false;
      }
      
      if (!/^1\d{10}$/.test(this.formData.receiverPhone)) {
        uni.showToast({
          title: '收件人电话格式不正确',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.formData.receiverAddress) {
        uni.showToast({
          title: '请输入收件人地址',
          icon: 'none'
        });
        return false;
      }
      
      if (parseFloat(this.formData.weight) <= 0) {
        uni.showToast({
          title: '请输入有效的包裹重量',
          icon: 'none'
        });
        return false;
      }
      
      if (parseFloat(this.formData.insuranceValue) < 0 || parseFloat(this.formData.insuranceValue) > 3000) {
        uni.showToast({
          title: '保价金额不能超过3000元',
          icon: 'none'
        });
        return false;
      }
      
      return true;
    },
    
    // 提交订单
    submitOrder() {
      if (!this.validateForm()) {
        return;
      }
      
      // 显示确认弹窗
      uni.showModal({
        title: '确认提交',
        content: `订单总金额：¥${this.calcTotalFee().toFixed(2)}，确认提交订单？`,
        success: (res) => {
          if (res.confirm) {
            // 显示加载中
            uni.showLoading({
              title: '提交中...',
              mask: true
            });
            
            // 模拟提交订单
            setTimeout(() => {
              uni.hideLoading();
              
              // 跳转到支付页面
              uni.navigateTo({
                url: `/pages/payment/payment?amount=${this.calcTotalFee().toFixed(2)}&method=${this.formData.paymentMethod}`,
                success: (res) => {
                  // 传递订单信息
                  res.eventChannel.emit('orderData', {
                    ...this.formData,
                    deliveryFee: this.calcDeliveryFee(),
                    insuranceFee: this.calcInsuranceFee(),
                    totalFee: this.calcTotalFee(),
                    orderTime: new Date().toLocaleString()
                  });
                }
              });
            }, 1000);
          }
        }
      });
    }
  }
};
</script>

<style>
.send-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 120rpx;
}

.form-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  padding: 30rpx;
  background-color: #fff;
}

.form-section {
  margin: 20rpx 0;
  background-color: #fff;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.form-header-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.address-book {
  font-size: 28rpx;
  color: #3cc51f;
}

.form-content {
  padding: 0 30rpx;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.form-item:last-child {
  border-bottom: none;
}

.form-label {
  width: 180rpx;
  font-size: 28rpx;
  color: #333;
}

.form-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.picker-view {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  color: #333;
}

.service-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.service-item:last-child {
  border-bottom: none;
}

.service-left {
  display: flex;
  flex-direction: column;
}

.service-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 6rpx;
}

.service-desc {
  font-size: 24rpx;
  color: #999;
}

.fee-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #333;
  border-bottom: 1rpx solid #f5f5f5;
}

.fee-item:last-child {
  border-bottom: none;
}

.total-fee {
  padding-top: 30rpx;
  margin-top: 10rpx;
  border-top: 1rpx solid #f5f5f5;
}

.price {
  color: #ff5a5f;
  font-weight: bold;
  font-size: 32rpx;
}

.payment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.payment-item:last-child {
  border-bottom: none;
}

.payment-left {
  display: flex;
  align-items: center;
}

.payment-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 20rpx;
}

.payment-name {
  font-size: 28rpx;
  color: #333;
}

.submit-section {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.total-info {
  display: flex;
  align-items: center;
}

.total-text {
  font-size: 28rpx;
  color: #333;
}

.total-price {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff5a5f;
}

.submit-btn {
  width: 300rpx;
  height: 80rpx;
  line-height: 80rpx;
  background-color: #3cc51f;
  color: #fff;
  font-size: 30rpx;
  border-radius: 40rpx;
}
</style> 