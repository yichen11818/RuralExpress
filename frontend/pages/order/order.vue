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
    <view class="order-list" v-if="orderList.length > 0">
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
    </view>
    
    <!-- 空状态 -->
    <view class="empty-state" v-else>
      <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
      <text class="empty-text">暂无订单数据</text>
      <button class="empty-btn" type="primary" @click="navigateTo('/pages/index/index')">去下单</button>
    </view>
  </view>
</template>

<script>
import { isLoggedIn, getUserInfo } from '@/api/auth';
import { getUserOrders, cancelOrder, getOrderStatusText } from '@/api/order';

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
      loading: false
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
    
    // 获取用户信息
    this.userInfo = getUserInfo();
    
    // 加载订单数据
    this.loadOrderData();
  },
  
  onPullDownRefresh() {
    // 刷新数据
    this.page = 1;
    this.loadOrderData();
    setTimeout(() => {
      uni.stopPullDownRefresh();
    }, 1000);
  },
  
  methods: {
    // 切换标签页
    switchTab(index) {
      if (this.currentTab === index) return;
      this.currentTab = index;
      this.page = 1;
      this.orderList = [];
      this.loadOrderData();
    },
    
    // 加载订单数据
    loadOrderData() {
      if (!this.userInfo || !this.userInfo.id) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }
      
      this.loading = true;
      
      // 查询参数
      const params = {
        page: this.page,
        size: this.size
      };
      
      // 根据标签页设置状态参数
      switch (this.currentTab) {
        case 1: // 待接单
          params.status = 0;
          break;
        case 2: // 处理中
          // 前端多次调用API，合并结果
          this.loadProcessingOrders();
          return;
        case 3: // 已完成
          params.status = 6;
          break;
      }
      
      // 调用API获取订单列表
      getUserOrders(this.userInfo.id, params)
        .then(res => {
          console.log('获取订单响应:', res);
          if (res.code === 200 && res.data) {
            // 合并数据
            if (this.page === 1) {
              this.orderList = res.data.records || [];
            } else {
              this.orderList = [...this.orderList, ...(res.data.records || [])];
            }
            this.total = res.data.total || 0;
          } else {
            uni.showToast({
              title: res.message || '获取订单失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取订单失败', err);
          uni.showToast({
            title: '获取订单失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 加载处理中的订单（包含多种状态）
    loadProcessingOrders() {
      if (!this.userInfo || !this.userInfo.id) return;
      
      const promises = [];
      
      // 依次加载状态为1-4的订单
      for (let status = 1; status <= 4; status++) {
        const params = {
          page: this.page,
          size: this.size,
          status
        };
        
        promises.push(getUserOrders(this.userInfo.id, params));
      }
      
      // 合并结果
      Promise.all(promises)
        .then(results => {
          let processingOrders = [];
          
          results.forEach(res => {
            if (res.code === 200 && res.data && res.data.records) {
              processingOrders = [...processingOrders, ...res.data.records];
            }
          });
          
          // 按创建时间排序
          processingOrders.sort((a, b) => {
            return new Date(b.createdAt) - new Date(a.createdAt);
          });
          
          // 更新数据
          if (this.page === 1) {
            this.orderList = processingOrders;
          } else {
            this.orderList = [...this.orderList, ...processingOrders];
          }
        })
        .catch(err => {
          console.error('获取处理中订单失败', err);
          uni.showToast({
            title: '获取订单失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 获取状态文本
    getStatusText(status) {
      return getOrderStatusText(status);
    },
    
    // 格式化手机号，中间4位显示*
    formatPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '****' + phone.substring(7);
    },
    
    // 格式化日期
    formatDate(dateStr) {
      return dateStr;
    },
    
    // 跳转到详情页
    navigateToDetail(id) {
      uni.navigateTo({
        url: `/pages/order/detail?id=${id}`
      });
    },
    
    // 取消订单
    cancelOrder(id) {
      uni.showModal({
        title: '提示',
        content: '确定要取消该订单吗？',
        success: (res) => {
          if (res.confirm) {
            // 调用取消订单API
            cancelOrder(id, '用户主动取消')
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '取消成功',
                    icon: 'success'
                  });
                  // 重新加载数据
                  this.loadOrderData();
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
    
    // 导航到指定页面
    navigateTo(url) {
      uni.switchTab({
        url
      });
    }
  }
};
</script>

<style>
.order-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 40rpx;
}

.tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx 0;
  margin-bottom: 20rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #3cc51f;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -10rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background-color: #3cc51f;
  border-radius: 2rpx;
}

.order-list {
  padding: 0 20rpx;
}

.order-item {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.order-no {
  font-size: 28rpx;
  color: #666;
}

.order-status {
  font-size: 28rpx;
  font-weight: bold;
}

.status-0 {
  color: #ff9900;
}

.status-1, .status-2, .status-3, .status-4 {
  color: #007aff;
}

.status-5 {
  color: #3cc51f;
}

.status-6 {
  color: #999;
}

.order-info {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.address-item {
  display: flex;
  margin-bottom: 20rpx;
}

.address-item:last-child {
  margin-bottom: 0;
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

.order-footer {
  padding-top: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-time {
  font-size: 26rpx;
  color: #999;
}

.order-actions {
  display: flex;
}

.action-btn {
  font-size: 26rpx;
  color: #666;
  padding: 10rpx 20rpx;
  border: 1rpx solid #ddd;
  border-radius: 30rpx;
  margin-left: 20rpx;
}

.primary-btn {
  color: #3cc51f;
  border-color: #3cc51f;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 30rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.empty-btn {
  width: 300rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  background-color: #3cc51f;
}
</style> 