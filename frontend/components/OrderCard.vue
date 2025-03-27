<template>
  <view class="order-card" @click="navigateToDetail">
    <view class="order-header">
      <view class="order-number">订单号：{{ orderData.orderNumber }}</view>
      <view class="order-status" :class="'status-' + orderData.status.toLowerCase()">
        {{ getStatusText(orderData.status) }}
      </view>
    </view>
    
    <view class="order-info">
      <view class="address-info">
        <view class="sender">
          <text class="label">寄</text>
          <text class="name">{{ orderData.senderName }}</text>
          <text class="phone">{{ formatPhone(orderData.senderPhone) }}</text>
        </view>
        <view class="address-line">{{ orderData.senderAddress }}</view>
        
        <view class="divider">
          <view class="line"></view>
        </view>
        
        <view class="receiver">
          <text class="label">收</text>
          <text class="name">{{ orderData.receiverName }}</text>
          <text class="phone">{{ formatPhone(orderData.receiverPhone) }}</text>
        </view>
        <view class="address-line">{{ orderData.receiverAddress }}</view>
      </view>
      
      <view class="order-details">
        <view class="detail-item">
          <text class="detail-label">下单时间</text>
          <text class="detail-value">{{ formatDate(orderData.createTime) }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">包裹类型</text>
          <text class="detail-value">{{ orderData.packageType }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">重量</text>
          <text class="detail-value">{{ orderData.weight }}kg</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">配送费</text>
          <text class="detail-value price">¥{{ orderData.price.toFixed(2) }}</text>
        </view>
      </view>
    </view>
    
    <view class="order-footer">
      <view class="order-time">{{ formatDate(orderData.createTime, 'full') }}</view>
      <view class="order-actions">
        <button 
          class="action-btn primary" 
          v-if="orderData.status === 'PENDING_PAYMENT'"
          @click.stop="handlePay"
        >
          去支付
        </button>
        <button 
          class="action-btn" 
          v-if="orderData.status === 'DELIVERED'"
          @click.stop="handleComplete"
        >
          确认收货
        </button>
        <button 
          class="action-btn" 
          v-if="orderData.status === 'COMPLETED' && !orderData.isRated"
          @click.stop="handleRate"
        >
          去评价
        </button>
        <button 
          class="action-btn" 
          @click.stop="handleTrack"
        >
          查看物流
        </button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'OrderCard',
  props: {
    orderData: {
      type: Object,
      required: true,
      default: () => ({})
    }
  },
  methods: {
    navigateToDetail() {
      uni.navigateTo({
        url: `/pages/order/detail?id=${this.orderData.id}`
      });
    },
    
    handlePay(e) {
      e.stopPropagation();
      this.$emit('pay', this.orderData.id);
    },
    
    handleComplete(e) {
      e.stopPropagation();
      this.$emit('complete', this.orderData.id);
    },
    
    handleRate(e) {
      e.stopPropagation();
      this.$emit('rate', this.orderData.id);
    },
    
    handleTrack(e) {
      e.stopPropagation();
      uni.navigateTo({
        url: `/pages/order/track?id=${this.orderData.id}`
      });
    },
    
    getStatusText(status) {
      const statusMap = {
        'PENDING_PAYMENT': '待付款',
        'PENDING_PICKUP': '待取件',
        'PICKED_UP': '已取件',
        'IN_TRANSIT': '配送中',
        'DELIVERED': '已送达',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      };
      return statusMap[status] || status;
    },
    
    formatPhone(phone) {
      if (!phone) return '';
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    },
    
    formatDate(dateString, type = 'short') {
      if (!dateString) return '';
      
      const date = new Date(dateString);
      
      if (type === 'short') {
        return `${date.getMonth() + 1}-${date.getDate()}`;
      } else {
        return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`;
      }
    }
  }
}
</script>

<style lang="scss">
.order-card {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    .order-number {
      font-size: 28rpx;
      color: #333;
    }
    
    .order-status {
      font-size: 26rpx;
      padding: 4rpx 16rpx;
      border-radius: 20rpx;
      
      &.status-pending_payment {
        color: #ff6600;
        background-color: rgba(255, 102, 0, 0.1);
      }
      
      &.status-pending_pickup, &.status-picked_up, &.status-in_transit {
        color: #2979ff;
        background-color: rgba(41, 121, 255, 0.1);
      }
      
      &.status-delivered {
        color: #0cae70;
        background-color: rgba(12, 174, 112, 0.1);
      }
      
      &.status-completed {
        color: #8f8f94;
        background-color: rgba(143, 143, 148, 0.1);
      }
      
      &.status-cancelled {
        color: #ff3b30;
        background-color: rgba(255, 59, 48, 0.1);
      }
    }
  }
  
  .order-info {
    background-color: #f8f8f8;
    border-radius: 8rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    
    .address-info {
      margin-bottom: 20rpx;
      
      .sender, .receiver {
        display: flex;
        align-items: center;
        margin-bottom: 8rpx;
        
        .label {
          width: 40rpx;
          height: 40rpx;
          line-height: 40rpx;
          text-align: center;
          background-color: #ff6600;
          color: #fff;
          border-radius: 50%;
          font-size: 24rpx;
          margin-right: 20rpx;
        }
        
        .name {
          font-size: 28rpx;
          color: #333;
          margin-right: 20rpx;
          font-weight: bold;
        }
        
        .phone {
          font-size: 26rpx;
          color: #666;
        }
      }
      
      .receiver .label {
        background-color: #2979ff;
      }
      
      .address-line {
        font-size: 26rpx;
        color: #666;
        padding-left: 60rpx;
        margin-bottom: 16rpx;
      }
      
      .divider {
        padding: 10rpx 0;
        display: flex;
        align-items: center;
        justify-content: center;
        
        .line {
          height: 2rpx;
          background-color: #ddd;
          width: 100%;
        }
      }
    }
    
    .order-details {
      display: flex;
      flex-wrap: wrap;
      
      .detail-item {
        width: 50%;
        margin-bottom: 16rpx;
        
        .detail-label {
          font-size: 24rpx;
          color: #999;
          margin-right: 8rpx;
        }
        
        .detail-value {
          font-size: 26rpx;
          color: #333;
          
          &.price {
            color: #ff6600;
            font-weight: bold;
          }
        }
      }
    }
  }
  
  .order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .order-time {
      font-size: 24rpx;
      color: #999;
    }
    
    .order-actions {
      display: flex;
      
      .action-btn {
        font-size: 24rpx;
        padding: 8rpx 20rpx;
        margin-left: 16rpx;
        background-color: #fff;
        border: 1rpx solid #ddd;
        border-radius: 30rpx;
        
        &.primary {
          background-color: #ff6600;
          color: #fff;
          border-color: #ff6600;
        }
      }
    }
  }
}
</style> 