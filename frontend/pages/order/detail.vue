<template>
  <view class="detail-container">
    <!-- 订单状态 -->
    <view class="status-section" :class="'status-bg-' + order.orderStatus">
      <view class="status-text">{{ getStatusText(order.orderStatus) }}</view>
      <view class="status-desc">{{ getStatusDesc(order.orderStatus) }}</view>
    </view>
    
    <!-- 物流信息 -->
    <view class="logistics-section" v-if="logistics.length > 0">
      <view class="section-title">物流信息</view>
      <view class="logistics-timeline">
        <view 
          class="logistics-item" 
          v-for="(item, index) in logistics" 
          :key="index"
          :class="{ 'active': index === 0 }"
        >
          <view class="timeline-dot"></view>
          <view class="timeline-line" v-if="index !== logistics.length - 1"></view>
          <view class="logistics-info">
            <view class="logistics-content">{{ item.content }}</view>
            <view class="logistics-time">{{ item.time }}</view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 配送信息 -->
    <view class="delivery-info-section">
      <view class="section-title">配送信息</view>
      <view class="info-item">
        <text class="item-label">配送方式</text>
        <text class="item-value">乡递快递</text>
      </view>
      <view class="info-item" v-if="order.courierName">
        <text class="item-label">配送员</text>
        <view class="courier-info">
          <text class="courier-name">{{ order.courierName }}</text>
          <text class="courier-phone">{{ formatPhone(order.courierPhone) }}</text>
          <view class="call-btn" @click="callCourier(order.courierPhone)">联系</view>
        </view>
      </view>
      <view class="info-item">
        <text class="item-label">订单编号</text>
        <text class="item-value">{{ order.orderNo }}</text>
      </view>
      <view class="info-item">
        <text class="item-label">下单时间</text>
        <text class="item-value">{{ order.createdAt }}</text>
      </view>
    </view>
    
    <!-- 地址信息 -->
    <view class="address-section">
      <view class="section-title">地址信息</view>
      <view class="address-item">
        <view class="address-icon sender">寄</view>
        <view class="address-content">
          <view class="address-user">
            <text class="user-name">{{ order.senderName }}</text>
            <text class="user-phone">{{ formatPhone(order.senderPhone) }}</text>
          </view>
          <text class="address-text">{{ order.senderAddress }}</text>
        </view>
      </view>
      <view class="address-divider"></view>
      <view class="address-item">
        <view class="address-icon receiver">收</view>
        <view class="address-content">
          <view class="address-user">
            <text class="user-name">{{ order.receiverName }}</text>
            <text class="user-phone">{{ formatPhone(order.receiverPhone) }}</text>
          </view>
          <text class="address-text">{{ order.receiverAddress }}</text>
        </view>
      </view>
    </view>
    
    <!-- 包裹信息 -->
    <view class="package-section">
      <view class="section-title">包裹信息</view>
      <view class="info-item">
        <text class="item-label">物品类型</text>
        <text class="item-value">{{ getPackageTypeText(order.packageType) }}</text>
      </view>
      <view class="info-item">
        <text class="item-label">重量</text>
        <text class="item-value">{{ order.weight }}kg</text>
      </view>
      <view class="info-item" v-if="order.note">
        <text class="item-label">备注</text>
        <text class="item-value">{{ order.note }}</text>
      </view>
    </view>
    
    <!-- 费用信息 -->
    <view class="cost-section">
      <view class="section-title">费用信息</view>
      <view class="info-item">
        <text class="item-label">配送费</text>
        <text class="item-value">¥{{ order.deliveryFee.toFixed(2) }}</text>
      </view>
      <view class="info-item">
        <text class="item-label">保价费</text>
        <text class="item-value">¥{{ order.insuranceFee.toFixed(2) }}</text>
      </view>
      <view class="info-item total-fee">
        <text class="item-label">合计</text>
        <text class="item-value price">¥{{ order.totalFee.toFixed(2) }}</text>
      </view>
    </view>
    
    <!-- 底部操作栏 -->
    <view class="footer-actions">
      <view 
        class="action-btn" 
        v-if="order.orderStatus === 0"
        @click="cancelOrder(order.id)"
      >
        取消订单
      </view>
      <view 
        class="action-btn primary-btn" 
        v-if="order.orderStatus === 5"
        @click="evaluateOrder(order.id)"
      >
        评价订单
      </view>
      <view 
        class="action-btn" 
        v-if="order.orderStatus === 5"
        @click="reorder()"
      >
        再次下单
      </view>
      <view 
        class="action-btn" 
        @click="contactService()"
      >
        联系客服
      </view>
    </view>
  </view>
</template>

<script>
import { isLoggedIn } from '@/api/auth';
import { getOrderDetail, cancelOrder } from '@/api/order';

export default {
  data() {
    return {
      orderId: null,
      order: {
        id: null,
        orderNo: '',
        orderStatus: 0,
        senderName: '',
        senderPhone: '',
        senderAddress: '',
        receiverName: '',
        receiverPhone: '',
        receiverAddress: '',
        courierName: '',
        courierPhone: '',
        packageType: 0,
        weight: 0,
        note: '',
        deliveryFee: 0,
        insuranceFee: 0,
        totalFee: 0,
        createdAt: ''
      },
      logistics: []
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
    
    // 获取订单ID
    if (options.id) {
      this.orderId = parseInt(options.id);
      this.getOrderDetail();
    }
  },
  
  methods: {
    // 获取订单详情
    getOrderDetail() {
      // 调用API获取订单详情数据
      uni.showLoading({
        title: '加载中...'
      });
      
      getOrderDetail(this.orderId)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.order = res.data.order || {};
            this.logistics = res.data.logistics || [];
          } else {
            uni.showToast({
              title: '获取订单详情失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取订单详情失败', err);
          uni.showToast({
            title: '获取订单详情失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    // 获取订单状态文本
    getStatusText(status) {
      const statusMap = {
        0: '待接单',
        1: '已接单',
        2: '取件中',
        3: '已取件',
        4: '配送中',
        5: '已完成',
        6: '已取消'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 获取订单状态描述
    getStatusDesc(status) {
      const descMap = {
        0: '等待快递员接单',
        1: '快递员已接单，即将取件',
        2: '快递员正在取件途中',
        3: '快递员已取件，即将发往目的地',
        4: '快递员正在配送途中',
        5: '订单已完成',
        6: '订单已取消'
      };
      return descMap[status] || '';
    },
    
    // 获取包裹类型文本
    getPackageTypeText(type) {
      const typeMap = {
        0: '普通快递',
        1: '文件',
        2: '食品',
        3: '电子产品',
        4: '易碎品',
        5: '其他'
      };
      return typeMap[type] || '普通快递';
    },
    
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '****' + phone.substring(7);
    },
    
    // 取消订单
    cancelOrder(id) {
      uni.showModal({
        title: '提示',
        content: '确定要取消该订单吗？',
        success: (res) => {
          if (res.confirm) {
            // 调用取消订单API
            uni.showLoading({
              title: '取消中...'
            });
            
            cancelOrder(id, '用户主动取消')
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '取消成功',
                    icon: 'success'
                  });
                  // 修改订单状态
                  this.order.orderStatus = 6; // 或者重新获取订单详情
                  // this.getOrderDetail();
                } else {
                  uni.showToast({
                    title: res.message || '取消失败',
                    icon: 'none'
                  });
                }
              })
              .catch(err => {
                console.error('取消订单失败', err);
                uni.showToast({
                  title: '取消失败',
                  icon: 'none'
                });
              })
              .finally(() => {
                uni.hideLoading();
              });
          }
        }
      });
    },
    
    // 评价订单
    evaluateOrder(id) {
      uni.navigateTo({
        url: `/pages/order/evaluate?id=${id}`
      });
    },
    
    // 再次下单
    reorder() {
      uni.navigateTo({
        url: '/pages/delivery/send',
        success: (res) => {
          // 传递订单数据
          res.eventChannel.emit('reorderData', {
            senderName: this.order.senderName,
            senderPhone: this.order.senderPhone,
            senderAddress: this.order.senderAddress,
            receiverName: this.order.receiverName,
            receiverPhone: this.order.receiverPhone,
            receiverAddress: this.order.receiverAddress,
            packageType: this.order.packageType,
            weight: this.order.weight
          });
        }
      });
    },
    
    // 联系客服
    contactService() {
      uni.makePhoneCall({
        phoneNumber: '4008007001',
        fail: () => {
          uni.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    },
    
    // 联系快递员
    callCourier(phone) {
      if (!phone) return;
      uni.makePhoneCall({
        phoneNumber: phone,
        fail: () => {
          uni.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    }
  }
};
</script>

<style>
.detail-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 120rpx;
}

.status-section {
  padding: 40rpx 30rpx;
  color: #fff;
}

.status-bg-0 {
  background-color: #ff9900;
}

.status-bg-1, .status-bg-2, .status-bg-3, .status-bg-4 {
  background-color: #007aff;
}

.status-bg-5 {
  background-color: #3cc51f;
}

.status-bg-6 {
  background-color: #999;
}

.status-text {
  font-size: 40rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.status-desc {
  font-size: 28rpx;
  opacity: 0.9;
}

.section-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.logistics-section, .delivery-info-section, .address-section, .package-section, .cost-section {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 0 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.logistics-timeline {
  padding: 30rpx 0;
}

.logistics-item {
  position: relative;
  padding-left: 40rpx;
  margin-bottom: 30rpx;
}

.logistics-item:last-child {
  margin-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: 0;
  top: 10rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background-color: #ccc;
}

.logistics-item.active .timeline-dot {
  background-color: #007aff;
  width: 20rpx;
  height: 20rpx;
  top: 8rpx;
  left: -2rpx;
}

.timeline-line {
  position: absolute;
  left: 8rpx;
  top: 20rpx;
  width: 2rpx;
  height: calc(100% + 30rpx);
  background-color: #eee;
}

.logistics-content {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 6rpx;
}

.logistics-item.active .logistics-content {
  color: #007aff;
  font-weight: bold;
}

.logistics-time {
  font-size: 24rpx;
  color: #999;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.item-label {
  font-size: 28rpx;
  color: #666;
}

.item-value {
  font-size: 28rpx;
  color: #333;
}

.courier-info {
  display: flex;
  align-items: center;
}

.courier-name {
  font-size: 28rpx;
  color: #333;
  margin-right: 10rpx;
}

.courier-phone {
  font-size: 28rpx;
  color: #666;
  margin-right: 20rpx;
}

.call-btn {
  font-size: 24rpx;
  color: #fff;
  background-color: #3cc51f;
  padding: 4rpx 16rpx;
  border-radius: 30rpx;
}

.address-item {
  display: flex;
  padding: 30rpx 0;
}

.address-divider {
  height: 1rpx;
  background-color: #f5f5f5;
  margin-left: 40rpx;
}

.address-icon {
  width: 40rpx;
  height: 40rpx;
  border-radius: 20rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24rpx;
  color: #fff;
  margin-right: 20rpx;
  margin-top: 6rpx;
}

.address-icon.sender {
  background-color: #007aff;
}

.address-icon.receiver {
  background-color: #3cc51f;
}

.address-content {
  flex: 1;
}

.address-user {
  display: flex;
  margin-bottom: 10rpx;
}

.user-name {
  font-size: 28rpx;
  color: #333;
  margin-right: 20rpx;
}

.user-phone {
  font-size: 28rpx;
  color: #666;
}

.address-text {
  font-size: 28rpx;
  color: #333;
}

.total-fee {
  border-top: 1rpx solid #f5f5f5;
  margin-top: 10rpx;
  padding-top: 30rpx;
}

.price {
  color: #ff5a5f;
  font-weight: bold;
  font-size: 32rpx;
}

.footer-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background-color: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.action-btn {
  font-size: 28rpx;
  color: #666;
  padding: 15rpx 30rpx;
  border: 1rpx solid #ddd;
  border-radius: 30rpx;
  margin-left: 20rpx;
}

.primary-btn {
  color: #3cc51f;
  border-color: #3cc51f;
}
</style> 