<template>
  <view class="order-container">
    <!-- 标签页 -->
    <view class="tabs">
      <view 
        class="tab-item" 
        v-for="(item, index) in tabs" 
        :key="index"
        :class="{ active: currentTab === index }"
        @click="switchTab(index)"
      >
        <text>{{ item.name }}</text>
      </view>
    </view>
    
    <!-- 订单列表 -->
    <scroll-view 
      scroll-y 
      class="order-list-scroll" 
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="order-list" v-if="orderList && orderList.length > 0">
        <view 
          class="order-item" 
          v-for="(item, index) in orderList" 
          :key="index"
          @click="navigateToDetail(item.id)"
        >
          <view class="order-header">
            <text class="order-no">订单号：{{ item.orderNo }}</text>
            <text class="order-status" :class="'status-' + item.orderStatus">{{ getStatusText(item.orderStatus) }}</text>
          </view>
          
          <view class="order-info">
            <view class="address-item">
              <view class="address-icon sender">寄</view>
              <view class="address-content">
                <view class="address-user">
                  <text class="user-name">{{ item.senderName }}</text>
                  <text class="user-phone">{{ formatPhone(item.senderPhone) }}</text>
                </view>
                <text class="address-text">{{ item.senderAddress }}</text>
              </view>
            </view>
            
            <view class="address-item">
              <view class="address-icon receiver">收</view>
              <view class="address-content">
                <view class="address-user">
                  <text class="user-name">{{ item.receiverName }}</text>
                  <text class="user-phone">{{ formatPhone(item.receiverPhone) }}</text>
                </view>
                <text class="address-text">{{ item.receiverAddress }}</text>
              </view>
            </view>
          </view>
          
          <view class="order-footer">
            <view class="order-time">
              <text>下单时间：{{ formatDate(item.createdAt) }}</text>
            </view>
            <view class="order-actions">
              <view 
                class="action-btn" 
                v-if="item.orderStatus === 0"
                @click.stop="cancelOrder(item.id)"
              >
                取消订单
              </view>
              <view 
                class="action-btn primary-btn" 
                v-if="item.orderStatus === 5"
                @click.stop="evaluateOrder(item.id)"
              >
                评价订单
              </view>
              <view 
                class="action-btn" 
                v-if="item.orderStatus === 5"
                @click.stop="navigateToDetail(item.id)"
              >
                再次下单
              </view>
            </view>
          </view>
        </view>
        
        <!-- 加载更多 -->
        <view class="load-more" v-if="orderList.length < total">
          <text v-if="loading">加载中...</text>
          <text v-else @click="loadMore">加载更多</text>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-state" v-else>
        <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
        <text class="empty-text">暂无订单数据</text>
        <button class="empty-btn" @click="navigateTo('/pages/index/index')">去下单</button>
      </view>
    </scroll-view>
    
    <!-- 加载提示 -->
    <view class="loading-overlay" v-if="userLoading">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text>加载中...</text>
      </view>
    </view>
  </view>
</template>

<script>
import { isLoggedIn } from '@/api/auth';
import { getUserOrders, cancelOrder, getOrderStatusText } from '@/api/order';
import { getUserProfile } from '@/api/user';

export default {
  data() {
    return {
      // 标签页
      tabs: [
        { name: '全部' },
        { name: '待接单' },
        { name: '处理中' },
        { name: '已完成' }
      ],
      
      // 当前标签页
      currentTab: 0,
      
      // 订单列表
      orderList: [],
      
      // 用户信息
      userInfo: null,
      
      // 分页信息
      page: 1,
      size: 10,
      total: 0,
      
      // 加载状态
      loading: false,
      userLoading: false,
      refreshing: false,
      
      // 订单状态
      statusFilter: 'all'
    };
  },
  
  // 页面显示
  onShow() {
    if (this.needRefresh) {
      this.page = 1;
      this.loadOrderData();
      this.needRefresh = false;
    }
  },
  
  // 生命周期
  onLoad(options) {
    // 处理状态参数
    if(options.status) {
      this.statusFilter = options.status;
      
      // 根据状态设置当前标签页
      switch(options.status) {
        case 'all':
          this.currentTab = 0;
          break;
        case 'pending':
          this.currentTab = 1;
          break;
        case 'processing':
          this.currentTab = 2;
          break;
        case 'completed':
          this.currentTab = 3;
          break;
        default:
          this.currentTab = 0;
      }
    }
    
    // 检查登录状态
    if (!isLoggedIn()) {
      uni.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    // 获取用户信息
    this.loadUserProfile();
  },
  
  onReady() {
    // 页面渲染完成
    console.log('订单列表页面渲染完成');
  },
  
  onUnload() {
    // 页面卸载
    console.log('订单列表页面卸载');
  },
  
  onPullDownRefresh() {
    // 刷新数据
    this.onRefresh();
  },
  
  onReachBottom() {
    // 触底加载更多
    this.loadMore();
  },
  
  methods: {
    // 刷新
    onRefresh() {
      this.refreshing = true;
      this.page = 1;
      
      this.loadOrderData(() => {
        this.refreshing = false;
        uni.stopPullDownRefresh();
      });
    },
    
    // 加载更多
    loadMore() {
      if (this.loading || this.orderList.length >= this.total) return;
      
      this.page++;
      this.loadOrderData();
    },
    
    // 获取用户资料
    loadUserProfile() {
      this.userLoading = true;
      
      uni.showLoading({
        title: '加载中...'
      });
      
      getUserProfile()
        .then(res => {
          if (res.code === 200 && res.data) {
            this.userInfo = res.data;
            // 加载订单数据
            this.loadOrderData();
          } else {
            uni.showToast({
              title: '获取用户信息失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取用户信息失败', err);
          uni.showToast({
            title: '获取用户信息失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
          this.userLoading = false;
        });
    },
    
    // 切换标签页
    switchTab(index) {
      if (this.currentTab === index) return;
      this.currentTab = index;
      this.page = 1;
      this.orderList = [];
      
      // 根据标签页设置状态过滤
      switch(index) {
        case 0:
          this.statusFilter = 'all';
          break;
        case 1:
          this.statusFilter = 'pending';
          break;
        case 2:
          this.statusFilter = 'processing';
          break;
        case 3:
          this.statusFilter = 'completed';
          break;
      }
      
      this.loadOrderData();
    },
    
    // 加载订单数据
    loadOrderData(callback) {
      if (!this.userInfo || !this.userInfo.id) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        if (typeof callback === 'function') callback();
        return;
      }
      
      this.loading = true;
      
      // 根据状态过滤订单
      let statusParam = null;
      if (this.statusFilter === 'pending') {
        statusParam = 0; // 待接单状态码
      } else if (this.statusFilter === 'processing') {
        statusParam = [1, 2, 3, 4]; // 处理中状态码数组
      } else if (this.statusFilter === 'completed') {
        statusParam = 6; // 已完成状态码
      }
      
      // 构建参数对象，只有当statusParam不为null时才包含status参数
      const params = {
        page: this.page,
        size: this.size
      };
      
      // 仅当statusParam不为null时才添加到params
      if (statusParam !== null) {
        params.status = statusParam;
      }
      
      // 调用API - 使用正确的参数格式
      getUserOrders(this.userInfo.id, params)
        .then(res => {
          if (res.code === 200 && res.data) {
            // 追加数据
            if (this.page === 1) {
              this.orderList = res.data.records || [];
            } else {
              this.orderList = [...this.orderList, ...(res.data.records || [])];
            }
            
            this.total = res.data.total || 0;
          } else {
            uni.showToast({
              title: '获取订单数据失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取订单数据失败', err);
          uni.showToast({
            title: '获取订单数据失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
          if (typeof callback === 'function') callback();
        });
    },
    
    // 取消订单
    cancelOrder(orderId) {
      uni.showModal({
        title: '提示',
        content: '确定要取消此订单吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '处理中...'
            });
            
            cancelOrder(orderId)
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '订单已取消',
                    icon: 'success'
                  });
                  
                  // 刷新数据
                  this.page = 1;
                  this.loadOrderData();
                } else {
                  uni.showToast({
                    title: res.message || '取消订单失败',
                    icon: 'none'
                  });
                }
              })
              .catch(err => {
                console.error('取消订单失败', err);
                uni.showToast({
                  title: '取消订单失败',
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
    evaluateOrder(orderId) {
      uni.navigateTo({
        url: `/pages/order/review?id=${orderId}`
      });
    },
    
    // 导航到详情页面
    navigateToDetail(orderId) {
      uni.navigateTo({
        url: `/pages/order/detail?id=${orderId}`
      });
    },
    
    // 导航到指定页面
    navigateTo(url) {
      uni.navigateTo({
        url
      });
    },
    
    // 获取状态文本
    getStatusText(status) {
      return getOrderStatusText(status);
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hour = date.getHours().toString().padStart(2, '0');
      const minute = date.getMinutes().toString().padStart(2, '0');
      
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    
    // 格式化手机号
    formatPhone(phone) {
      if (!phone) return '';
      
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    }
  }
};
</script>

<style lang="scss">
.order-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f8f8f8;
  
  .tabs {
    display: flex;
    justify-content: space-around;
    background-color: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    position: sticky;
    top: 0;
    z-index: 10;
    padding: 0;
    margin: 0;
    
    .tab-item {
      position: relative;
      padding: 30rpx 0;
      font-size: 28rpx;
      color: #666;
      flex: 1;
      text-align: center;
      
      &.active {
        color: #FF6B00;
        font-weight: bold;
        
        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 40rpx;
          height: 4rpx;
          background-color: #FF6B00;
          border-radius: 2rpx;
        }
      }
    }
  }
  
  .order-list-scroll {
    flex: 1;
    height: calc(100vh - 80rpx); /* 减去tab高度 */
  }
  
  .order-list {
    padding: 30rpx;
    
    .order-item {
      margin-bottom: 30rpx;
      padding: 30rpx;
      background-color: #fff;
      border-radius: 16rpx;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      
      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-bottom: 20rpx;
        border-bottom: 1px solid #f2f2f2;
        
        .order-no {
          font-size: 24rpx;
          color: #999;
        }
        
        .order-status {
          font-size: 24rpx;
          
          &.status-0 {
            color: #FF6B00;
          }
          
          &.status-1, &.status-2, &.status-3, &.status-4 {
            color: #3F97FF;
          }
          
          &.status-5 {
            color: #3FBC7C;
          }
          
          &.status-6 {
            color: #F56C6C;
          }
        }
      }
      
      .order-info {
        padding: 20rpx 0;
        
        .address-item {
          display: flex;
          margin-bottom: 20rpx;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .address-icon {
            width: 48rpx;
            height: 48rpx;
            line-height: 48rpx;
            text-align: center;
            border-radius: 50%;
            font-size: 24rpx;
            color: #fff;
            margin-right: 20rpx;
            flex-shrink: 0;
            
            &.sender {
              background-color: #FF6B00;
            }
            
            &.receiver {
              background-color: #3F97FF;
            }
          }
          
          .address-content {
            flex: 1;
            
            .address-user {
              display: flex;
              align-items: center;
              margin-bottom: 8rpx;
              
              .user-name {
                font-size: 28rpx;
                font-weight: bold;
                color: #333;
                margin-right: 20rpx;
              }
              
              .user-phone {
                font-size: 24rpx;
                color: #999;
              }
            }
            
            .address-text {
              font-size: 26rpx;
              color: #666;
              line-height: 1.4;
            }
          }
        }
      }
      
      .order-footer {
        padding-top: 20rpx;
        border-top: 1px solid #f2f2f2;
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
            margin-left: 20rpx;
            padding: 10rpx 30rpx;
            font-size: 24rpx;
            color: #666;
            background-color: #f8f8f8;
            border-radius: 32rpx;
            
            &.primary-btn {
              color: #fff;
              background-color: #FF6B00;
            }
          }
        }
      }
    }
    
    .load-more {
      text-align: center;
      padding: 20rpx 0;
      color: #999;
      font-size: 24rpx;
    }
  }
  
  .empty-state {
    padding-top: 100rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .empty-image {
      width: 320rpx;
      height: 320rpx;
      margin-bottom: 30rpx;
    }
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
      margin-bottom: 40rpx;
    }
    
    .empty-btn {
      background-color: #FF6B00;
      border-radius: 40rpx;
      width: 320rpx;
      height: 80rpx;
      line-height: 80rpx;
      font-size: 28rpx;
      color: #fff;
    }
  }
  
  .loading-overlay {
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 999;
    
    .loading-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      background-color: rgba(0, 0, 0, 0.6);
      padding: 40rpx;
      border-radius: 16rpx;
      
      .loading-spinner {
        width: 60rpx;
        height: 60rpx;
        border: 6rpx solid #f3f3f3;
        border-top: 6rpx solid #FF6B00;
        border-radius: 50%;
        animation: spin 1s linear infinite;
        margin-bottom: 20rpx;
      }
      
      text {
        color: #fff;
        font-size: 28rpx;
      }
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 